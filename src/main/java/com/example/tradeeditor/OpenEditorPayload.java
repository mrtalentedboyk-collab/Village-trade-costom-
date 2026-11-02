package com.example.tradeeditor;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenEditorPayload(String data) implements CustomPayload {
    public static final Id<OpenEditorPayload> ID = new Id<>(Identifier.of("tradeeditor", "open"));
    public static final PacketCodec<RegistryByteBuf, OpenEditorPayload> CODEC =
            PacketCodec.tuple(PacketCodecs.STRING, OpenEditorPayload::data, OpenEditorPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
