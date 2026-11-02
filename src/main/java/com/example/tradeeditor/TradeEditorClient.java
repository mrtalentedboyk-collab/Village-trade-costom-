package com.example.tradeeditor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class TradeEditorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(OpenEditorPayload.ID, (payload, context) ->
                context.client().setScreen(new TradeEditorScreen(payload.data())));
    }
}
