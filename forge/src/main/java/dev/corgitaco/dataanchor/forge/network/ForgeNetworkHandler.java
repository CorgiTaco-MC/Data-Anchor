/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.forge.network;

import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.register.PacketRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class ForgeNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    protected static final Map<Class<? extends Packet>, SimpleChannel> channels = new ConcurrentHashMap<>();
    protected static final Map<Class<? extends Packet>, ResourceLocation> channelNames  = new ConcurrentHashMap<>();

    public ForgeNetworkHandler() {
    }

    public <T extends Packet> void registerMessage(ResourceLocation location, Packet.Handler<T> handler) {
        SimpleChannel simpleChannel = channels.computeIfAbsent(handler.clazz(), aClass -> NetworkRegistry.newSimpleChannel(
                location,
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        ));

        channelNames.put(handler.clazz(), location);

        simpleChannel.registerMessage(0, handler.clazz(), handler.write(), handler.read(), (t, contextSupplier) -> handle(t, contextSupplier, handler.handle()));
    }

    public <T extends Packet> void handle(T packet, Supplier<NetworkEvent.Context> ctx, Packet.Handle<T> handle) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> Client.clientHandle(packet, handle));
        } else {
            ServerPlayer sender = context.getSender();
            handle.handle(packet, sender != null ? sender.level() : null, sender);
        }
        context.setPacketHandled(true);
    }

    private static class Client {
        private static <T extends Packet> void clientHandle(T packet, Packet.Handle<T> handle) {
            handle.handle(packet, Minecraft.getInstance().level, Minecraft.getInstance().player);
        }
    }
}