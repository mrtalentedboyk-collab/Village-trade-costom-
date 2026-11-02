package com.example.tradeeditor;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TradeEditorScreen extends Screen {
    private static final int MAX_TRADES = 10;
    private final List<String[]> rows = new ArrayList<>();
    private final List<TextFieldWidget[]> fields = new ArrayList<>();

    public TradeEditorScreen(String data) {
        super(Text.literal("Villager Trade Editor"));
        for (String line : data.split("\n")) {
            if (line.isBlank()) continue;
            String[] p = line.split("\\|", -1);
            if (p.length >= 7) rows.add(p);
        }
    }

    @Override
    protected void init() {
        fields.clear();
        int[] w = {92, 26, 92, 26, 92, 26, 30};
        int gap = 3, total = 18 + gap;
        for (int x : w) total += x + gap;
        int startX = (this.width - total) / 2;
        int y = 36;

        for (int r = 0; r < rows.size(); r++) {
            TextFieldWidget[] row = new TextFieldWidget[7];
            int x = startX;
            for (int c = 0; c < 7; c++) {
                TextFieldWidget f = new TextFieldWidget(this.textRenderer, x, y, w[c], 16, Text.empty());
                f.setMaxLength(100);
                f.setText(rows.get(r)[c]);
                this.addDrawableChild(f);
                row[c] = f;
                x += w[c] + gap;
            }
            fields.add(row);
            final int idx = r;
            this.addDrawableChild(ButtonWidget.builder(Text.literal("X"), b -> {
                collect();
                rows.remove(idx);
                this.clearAndInit();
            }).dimensions(x, y, 18, 16).build());
            y += 20;
        }

        int by = this.height - 28;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("+ Add Trade"), b -> {
            if (rows.size() >= MAX_TRADES) return;
            collect();
            rows.add(new String[]{"minecraft:emerald", "1", "", "0", "minecraft:diamond", "1", "12"});
            this.clearAndInit();
        }).dimensions(this.width / 2 - 155, by, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> {
            collect();
            ClientPlayNetworking.send(new SaveTradesPayload(encode()));
            this.close();
        }).dimensions(this.width / 2 - 50, by, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> this.close())
                .dimensions(this.width / 2 + 55, by, 100, 20).build());
    }

    private void collect() {
        for (int r = 0; r < fields.size() && r < rows.size(); r++)
            for (int c = 0; c < 7; c++) rows.get(r)[c] = fields.get(r)[c].getText();
    }

    private String encode() {
        StringBuilder sb = new StringBuilder();
        for (String[] r : rows) sb.append(String.join("|", r)).append('\n');
        return sb.toString();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Buy item | qty | Buy2 (optional) | qty | Sell item | qty | max uses"),
                this.width / 2, 22, 0xAAAAAA);
    }
}
