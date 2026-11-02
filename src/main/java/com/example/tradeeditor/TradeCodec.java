package com.example.tradeeditor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;

import java.util.Optional;

public final class TradeCodec {
    private TradeCodec() {}

    public static String serialize(TradeOfferList offers) {
        StringBuilder sb = new StringBuilder();
        for (TradeOffer o : offers) {
            ItemStack a = o.getOriginalFirstBuyItem();
            String b2 = "";
            int b2c = 0;
            Optional<TradedItem> second = o.getSecondBuyItem();
            if (second.isPresent()) {
                b2 = Registries.ITEM.getId(second.get().item().value()).toString();
                b2c = second.get().count();
            }
            ItemStack s = o.getSellItem();
            sb.append(Registries.ITEM.getId(a.getItem())).append('|').append(a.getCount()).append('|')
              .append(b2).append('|').append(b2c).append('|')
              .append(Registries.ITEM.getId(s.getItem())).append('|').append(s.getCount()).append('|')
              .append(o.getMaxUses()).append('\n');
        }
        return sb.toString();
    }

    public static TradeOfferList parse(String data) {
        TradeOfferList list = new TradeOfferList();
        for (String line : data.split("\n")) {
            if (line.isBlank()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length < 7) continue;
            Item buy = item(p[0]);
            Item sell = item(p[4]);
            if (buy == null || sell == null) continue;
            int bc = clamp(p[1], 1, 64);
            int sc = clamp(p[5], 1, 64);
            int uses = clamp(p[6], 1, 9999);
            Item buy2 = item(p[2]);
            Optional<TradedItem> second = buy2 == null
                    ? Optional.empty()
                    : Optional.of(new TradedItem(buy2, clamp(p[3], 1, 64)));
            list.add(new TradeOffer(new TradedItem(buy, bc), second, new ItemStack(sell, sc), uses, 0, 0.0f));
        }
        return list;
    }

    private static Item item(String id) {
        if (id == null || id.isBlank()) return null;
        Identifier ident = Identifier.tryParse(id.trim().contains(":") ? id.trim() : "minecraft:" + id.trim());
        if (ident == null || !Registries.ITEM.containsId(ident)) return null;
        Item it = Registries.ITEM.get(ident);
        return it == net.minecraft.item.Items.AIR ? null : it;
    }

    private static int clamp(String s, int min, int max) {
        try { return Math.max(min, Math.min(max, Integer.parseInt(s.trim()))); }
        catch (Exception e) { return min; }
    }
}
