package com.example.tradeeditor;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SaveTradesPayload(String data) implements CustomPayload {
    public static final Id<SaveTradesPayload> ID = new Id<>(Identifier.of("tradeeditor", "save"));
    public static final PacketCodec<RegistryByteBuf, SaveTradesPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.STRING, SaveTradesPayload::data, SaveTradesPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
