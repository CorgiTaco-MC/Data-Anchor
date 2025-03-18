/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.fabric.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.network.C2SNetworkContainer;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.C2SPacketBroadcaster;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;
import java.util.function.Function;

@AutoService(C2SPacketBroadcaster.class)
public class C2SFabricPacketBroadcaster extends FabricPacketBroadcaster implements C2SPacketBroadcaster {

    @Override
    public void registerPackets() {
        C2SNetworkContainer.C2S_NAMESPACED_CONTAINERS.forEach((modId, networkContainer) -> networkContainer.registerMessages(this::register));
    }

    @Override
    public <T extends Packet> void registerReceiver(ResourceLocation id, Function<FriendlyByteBuf, T> decode, Packet.Handle<T> handler) {
        registerServerReceiver(id, decode, handler);
    }

    public <MSG extends Packet> void sendToServer(MSG packet) {
        ResourceLocation packetId = packetIds.get(packet.getClass());
        @SuppressWarnings("unchecked")
        BiConsumer<MSG, FriendlyByteBuf> encoder = (BiConsumer<MSG, FriendlyByteBuf>) encoders.get(packet.getClass());
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        encoder.accept(packet, buf);
        ClientPlayNetworking.send(packetId, buf);
    }

    protected static <T extends Packet> void registerServerReceiver(ResourceLocation id, Function<FriendlyByteBuf, T> decode, Packet.Handle<T> handler) {
        ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler1, buf, responseSender) -> {
            buf.retain();
            server.execute(() -> {
                T packet = decode.apply(buf);
                Level level = player.level();
                try {
                    handler.handle(packet, level, player);
                } catch (Throwable throwable) {
                    DataAnchor.LOGGER.error("Packet \"%s\" failed: ".formatted(id.toString()), throwable);
                    throw throwable;
                }
                buf.release();
            });
        });
    }

}