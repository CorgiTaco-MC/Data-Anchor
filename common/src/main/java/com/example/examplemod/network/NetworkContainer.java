package com.example.examplemod.network;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class NetworkContainer {
    public static final Map<String, NetworkContainer> NAMESPACED_CONTAINERS = new HashMap<>();

    private final Map<ResourceLocation, Packet.Handler<? extends Packet>> packets = new HashMap<>();
    private final String nameSpace;
    private boolean locked = false;

    private NetworkContainer(String namespace) {
        this.nameSpace = namespace;
    }

    public static NetworkContainer of(String namespace) {
        NetworkContainer networkContainer = NAMESPACED_CONTAINERS.get(namespace);
        if (networkContainer != null) {
            return networkContainer;
        }

        NetworkContainer networkContainer1 = new NetworkContainer(namespace);
        NAMESPACED_CONTAINERS.put(namespace, networkContainer1);
        return networkContainer1;
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
