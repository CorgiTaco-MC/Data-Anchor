package com.example.examplemod.forge.network;

import com.example.examplemod.network.NetworkContainer;
import com.example.examplemod.network.Packet;
import com.example.examplemod.network.PacketBroadcaster;
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

@AutoService(PacketBroadcaster.class)
public class ForgeNetworkHandler implements PacketBroadcaster {
    private static final String PROTOCOL_VERSION = "1";

    private final Map<Class<? extends Packet>, SimpleChannel> channels = new ConcurrentHashMap<>();

    private ForgeNetworkHandler() {
    }

    @Override
    public void registerPackets() {
        NetworkContainer.NAMESPACED_CONTAINERS.forEach((s, networkContainer) -> networkContainer.registerMessages(this::registerMessage));
    }

    public <T extends Packet> void registerMessage(ResourceLocation location, Packet.Handler<T> handler) {
        channels.computeIfAbsent(handler.clazz(), aClass -> NetworkRegistry.newSimpleChannel(
                location,
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        )).registerMessage(0, handler.clazz(), handler.write(), handler.read(), (t, contextSupplier) -> handle(t, contextSupplier, handler.handle()));
    }

    public <T extends Packet> void sendToServer(T packet) {
        channels.get(packet.getClass()).sendToServer(packet);
    }

    @Override
    public <MSG extends Packet> void sendToPlayer(MSG msg, ServerPlayer player) {
        channels.get(msg.getClass()).send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayers(MSG msg) {
        channels.get(msg.getClass()).send(PacketDistributor.ALL.noArg(), msg);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ResourceKey<Level> dimensionKey) {
        channels.get(msg.getClass()).send(PacketDistributor.DIMENSION.with(() -> dimensionKey), msg);
    }

    @Override
    public <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ResourceKey<Level> dimensionKey, double x, double y, double z, double radius) {
        channels.get(msg.getClass()).send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, dimensionKey)), msg);
    }

    @Override
    public <MSG extends Packet> void trackingEntity(MSG msg, Entity entity) {
        channels.get(msg.getClass()).send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
    }

    @Override
    public <MSG extends Packet> void trackingEntityAndSelf(MSG msg, Entity entity) {
        channels.get(msg.getClass()).send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
    }

    @Override
    public <MSG extends Packet> void trackingChunk(MSG msg, LevelChunk chunk) {
        channels.get(msg.getClass()).send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
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