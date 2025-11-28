/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.network;

import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class NetworkContainer implements PacketBroadcaster {

    private final Map<ResourceLocation, Packet.Handler<? extends Packet>> packets = new HashMap<>();
    private final String nameSpace;
    private boolean locked = false;

    public NetworkContainer(String namespace) {
        this.nameSpace = namespace;
    }


    public <T extends Packet> void registerPacketHandler(String name, Class<T> packet) {
        registerPacketHandler(name, new Packet.Handler<>(packet));
    }

    public <T extends Packet> void registerPacketHandler(String name, Class<T> clazz, BiConsumer<T, FriendlyByteBuf> write,
                                                         Function<FriendlyByteBuf, T> read,
                                                         Packet.Handle<T> handle) {
        registerPacketHandler(name, new Packet.Handler<>(clazz, write, read, handle));
    }

    public <T extends Packet> void registerPacketHandler(String name, Packet.Handler<T> packetHandle) {
        if (!locked) {
            this.packets.put(new ResourceLocation(this.nameSpace, name), packetHandle);
        } else {
            throw new IllegalArgumentException("Network Container for namespace \"%s\" is already locked, try registering earlier!".formatted(this.nameSpace));
        }
    }

    public void registerMessages(BiConsumer<ResourceLocation, Packet.Handler<? extends Packet>> handlerConsumer) {
        this.packets.forEach(handlerConsumer);
        this.locked = true;
    }
}
