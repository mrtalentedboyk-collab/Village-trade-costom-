package com.example.tradeeditor;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TradeEditorMod implements ModInitializer {
    private static final Map<UUID, VillagerEntity> EDITING = new ConcurrentHashMap<>();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(OpenEditorPayload.ID, OpenEditorPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SaveTradesPayload.ID, SaveTradesPayload.CODEC);

        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
            if (!(entity instanceof VillagerEntity villager)) return ActionResult.PASS;
            if (!player.isCreative()) return ActionResult.PASS;

            if (!world.isClient() && player instanceof ServerPlayerEntity sp) {
                EDITING.put(sp.getUuid(), villager);
                String data = TradeCodec.serialize(villager.getOffers());
                ServerPlayNetworking.send(sp, new OpenEditorPayload(data));
            }
            return ActionResult.SUCCESS;
        });

        ServerPlayNetworking.registerGlobalReceiver(SaveTradesPayload.ID, (payload, context) -> {
            ServerPlayerEntity sp = context.player();
            if (!sp.isCreative()) return;
            VillagerEntity villager = EDITING.get(sp.getUuid());
            if (villager == null || villager.isRemoved()) return;
            villager.setOffersFromServer(TradeCodec.parse(payload.data()));
        });
    }
}
