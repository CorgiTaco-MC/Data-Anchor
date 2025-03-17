package dev.corgitaco.dataanchor.network;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class NetworkContainer {

    private final Map<ResourceLocation, Packet.Handler<? extends Packet>> packets = new HashMap<>();
    private final String nameSpace;
    private boolean locked = false;

    public NetworkContainer(String namespace) {
        this.nameSpace = namespace;
    }

    public <T extends Packet> void registerPacketHandler(Packet.Handler<T> packetHandle) {
        if (!locked) {
            ResourceLocation id = packetHandle.type().id();
            if (!id.getNamespace().equals(this.nameSpace)) {
                throw new IllegalArgumentException("Network Container for namespace \"%s\" cannot register packet with namespace \"%s\", expected namespace \"%s\"".formatted(this.nameSpace, id, this.nameSpace));
            }
            this.packets.put(id, packetHandle);
        } else {
            throw new IllegalArgumentException("Network Container for namespace \"%s\" is already locked, try registering earlier!".formatted(this.nameSpace));
        }
    }

    public void registerMessages(Consumer<Packet.Handler<? extends Packet>> handlerConsumer) {
        this.packets.values().forEach(handlerConsumer);
        this.locked = true;
    }
}
