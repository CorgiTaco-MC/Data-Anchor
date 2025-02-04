package com.example.examplemod.fabric.network;

import com.example.examplemod.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class FabricPacketBroadcaster {

    protected final Map<Class<? extends Packet>, BiConsumer<?, FriendlyByteBuf>> encoders = new ConcurrentHashMap<>();
    protected final Map<Class<? extends Packet>, ResourceLocation> packetIds = new ConcurrentHashMap<>();

    protected final <T extends Packet> void register(ResourceLocation path, Packet.Handler<T> handler) {
        registerMessage(path, handler.clazz(), handler.write(), handler.read(), handler.handle());
    }

    private <T extends Packet> void registerMessage(ResourceLocation id,
                                                    Class<T> clazz,
                                                    BiConsumer<T, FriendlyByteBuf> encode,
                                                    Function<FriendlyByteBuf, T> decode,
                                                    Packet.Handle<T> handler) {
        encoders.put(clazz, encode);
        packetIds.put(clazz, id);
        registerReceiver(id, decode, handler);
    }

    public abstract <T extends Packet> void registerReceiver(ResourceLocation id, Function<FriendlyByteBuf, T> decode, Packet.Handle<T> handler);
}