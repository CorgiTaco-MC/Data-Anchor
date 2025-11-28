/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.fabric.network;

import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import dev.corgitaco.dataanchor.network.register.PacketRegister;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class FabricPacketBroadcaster {

    protected final <T extends Packet> void register(Packet.Handler<T> handler) {
        registerPayload(handler.type(), handler.serializer());
        registerHandler(handler);
    }

    protected abstract <T extends Packet> void registerPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> serializer);

    protected abstract <T extends Packet> void registerHandler(Packet.Handler<T> handler);

    public record ClientProxy() {
        public static <T extends Packet> void registerClientReceiver(Packet.Handler<T> handler) {
            ClientPlayNetworking.registerGlobalReceiver(handler.type(), (t, context) -> handler.handle().handle(t, context.client().level, context.player()));
        }
    }

    public static class ServerProxy {
        static <T extends Packet> void registerServerReceiver(Packet.Handler<T> handler) {
            ServerPlayNetworking.registerGlobalReceiver(handler.type(), (t, context) -> handler.handle().handle(t, context.player().level(), context.player()));
        }
    }
}