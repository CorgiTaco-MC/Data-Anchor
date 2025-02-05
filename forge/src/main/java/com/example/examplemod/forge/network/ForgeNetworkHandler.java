package com.example.examplemod.forge.network;

import com.example.examplemod.network.S2CNetworkContainer;
import com.example.examplemod.network.Packet;
import com.example.examplemod.network.broadcast.BiDirectionalPacketBroadcaster;
import com.example.examplemod.network.broadcast.C2SPacketBroadcaster;
import com.example.examplemod.network.broadcast.S2CPacketBroadcaster;
import com.google.auto.service.AutoService;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class ForgeNetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    protected final Map<Class<? extends Packet>, SimpleChannel> channels = new ConcurrentHashMap<>();

    public ForgeNetworkHandler() {
    }

    public <T extends Packet> void registerMessage(ResourceLocation location, Packet.Handler<T> handler) {
        channels.computeIfAbsent(handler.clazz(), aClass -> NetworkRegistry.newSimpleChannel(
                location,
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        )).registerMessage(0, handler.clazz(), handler.write(), handler.read(), (t, contextSupplier) -> handle(t, contextSupplier, handler.handle()));
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