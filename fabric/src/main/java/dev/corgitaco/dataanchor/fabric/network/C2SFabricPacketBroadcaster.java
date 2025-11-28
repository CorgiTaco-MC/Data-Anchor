/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.fabric.network;

import com.google.auto.service.AutoService;
import dev.corgitaco.dataanchor.network.C2SNetworkContainer;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.C2SPacketBroadcaster;
import dev.corgitaco.dataanchor.network.register.C2SPacketRegister;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

@AutoService({C2SPacketBroadcaster.class, C2SPacketRegister.class})
public class C2SFabricPacketBroadcaster extends FabricPacketBroadcaster implements C2SPacketBroadcaster, C2SPacketRegister {

    @Override
    public void registerPackets() {
        C2SNetworkContainer.C2S_NAMESPACED_CONTAINERS.forEach((modId, networkContainer) -> networkContainer.registerMessages(this::register));
    }

    @Override
    protected <T extends Packet> void registerPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> serializer) {
        PayloadTypeRegistry.playC2S().register(type, serializer);
    }

    @Override
    protected <T extends Packet> void registerHandler(Packet.Handler<T> handler) {
        ServerProxy.registerServerReceiver(handler);
    }

    public <MSG extends Packet> void sendToServer(MSG packet) {
        ClientPlayNetworking.send(packet);
    }
}