package com.example.examplemod.fabric.network;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.fabric.ExampleModFabric;
import com.example.examplemod.network.NetworkContainer;
import com.example.examplemod.network.Packet;
import com.example.examplemod.network.PacketBroadcaster;
import com.google.auto.service.AutoService;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

@AutoService(PacketBroadcaster.class)
public class FabricNetworkHandler implements PacketBroadcaster {

    private final Map<Class<? extends Packet>, BiConsumer<?, FriendlyByteBuf>> ENCODERS = new ConcurrentHashMap<>();
    private final Map<Class<? extends Packet>, ResourceLocation> PACKET_IDS = new ConcurrentHashMap<>();

    private <T extends Packet> void register(ResourceLocation path, Packet.Handler<T> handler) {
        registerMessage(path, handler.clazz(), handler.direction(), handler.write(), handler.read(), handler.handle());
    }

    private <T extends Packet> void registerMessage(ResourceLocation id,
                                                    Class<T> clazz,
                                                    Packet.PacketDirection direction,
                                                    BiConsumer<T, FriendlyByteBuf> encode,
                                                    Function<FriendlyByteBuf, T> decode,
                                                    Packet.Handle<T> handler) {
        ENCODERS.put(clazz, encode);
        PACKET_IDS.put(clazz, id);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && direction == Packet.PacketDirection.SERVER_TO_CLIENT) {
            ClientProxy.registerClientReceiver(id, decode, handler);
        }
        if (direction == Packet.PacketDirection.CLIENT_TO_SERVER) {
            ServerProxy.registerServerReceiver(id, decode, handler);
        }
    }

    @Override
    public void registerPackets() {
        NetworkContainer.NAMESPACED_CONTAINERS.forEach((modId, networkContainer) -> networkContainer.registerMessages(this::register));
    }

    public <MSG extends Packet> void sendToServer(MSG packet) {
        ResourceLocation packetId = PACKET_IDS.get(packet.getClass());
        @SuppressWarnings("unchecked")
        BiConsumer<MSG, FriendlyByteBuf> encoder = (BiConsumer<MSG, FriendlyByteBuf>) ENCODERS.get(packet.getClass());
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        encoder.accept(packet, buf);
        ClientPlayNetworking.send(packetId, buf);
    }

    @Override
    public <MSG extends Packet> void sendToPlayer(MSG msg, ServerPlayer player) {
        ResourceLocation packetId = PACKET_IDS.get(msg.getClass());
        @SuppressWarnings("unchecked")
        BiConsumer<MSG, FriendlyByteBuf> encoder = (BiConsumer<MSG, FriendlyByteBuf>) ENCODERS.get(msg.getClass());
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        encoder.accept(msg, buf);
        ServerPlayNetworking.send(player, packetId, buf);
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayers(MSG msg) {
        MinecraftServer server = ExampleModFabric.server;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendToPlayer(msg, player);
        }
    }

    @Override
    public <MSG extends Packet> void sendToAllPlayersInDimension(MSG msg, ResourceKey<Level> dimensionKey) {
        MinecraftServer server = ExampleModFabric.server;
        for (ServerPlayer player : server.getLevel(dimensionKey).players()) {
            sendToPlayer(msg, player);
        }

    }

    @Override
    public <MSG extends Packet> void sendNearPositionInDimension(MSG msg, ResourceKey<Level> dimensionKey, double x, double y, double z, double radius) {
        MinecraftServer server = ExampleModFabric.server;
        for (ServerPlayer player : server.getLevel(dimensionKey).players()) {
            if (player.distanceToSqr(x, y, z) <= Mth.square(radius)) {
                sendToPlayer(msg, player);
            }
        }

    }

    @Override
    public <MSG extends Packet> void trackingEntity(MSG msg, Entity entity) {
        MinecraftServer server = ExampleModFabric.server;
        ServerLevel level = server.getLevel(entity.level().dimension());
        ChunkMap.TrackedEntity trackedEntity = level.getChunkSource().chunkMap.entityMap.get(entity.getId());

        if (trackedEntity != null) {
            for (ServerPlayerConnection serverPlayerConnection : trackedEntity.seenBy) {
                ServerPlayer player = serverPlayerConnection.getPlayer();
                sendToPlayer(msg, player);
            }
        }


    }

    @Override
    public <MSG extends Packet> void trackingEntityAndSelf(MSG msg, Entity entity) {
        MinecraftServer server = ExampleModFabric.server;
        ServerLevel level = server.getLevel(entity.level().dimension());
        ChunkMap.TrackedEntity trackedEntity = level.getChunkSource().chunkMap.entityMap.get(entity.getId());

        if (trackedEntity != null) {
            for (ServerPlayerConnection serverPlayerConnection : trackedEntity.seenBy) {
                ServerPlayer player = serverPlayerConnection.getPlayer();
                sendToPlayer(msg, player);
            }
            if (trackedEntity.entity instanceof ServerPlayer serverPlayer) {
                sendToPlayer(msg, serverPlayer);
            }

        }

    }

    @Override
    public <MSG extends Packet> void trackingChunk(MSG msg, LevelChunk chunk) {
        MinecraftServer server = ExampleModFabric.server;
        ServerLevel level = server.getLevel(chunk.getLevel().dimension());
        ChunkMap chunkMap = level.getChunkSource().chunkMap;

        chunkMap.getPlayers(chunk.getPos(), false).forEach(serverPlayer -> sendToPlayer(msg, serverPlayer));
    }

    public record ClientProxy() {

        public static <T extends Packet> void registerClientReceiver(ResourceLocation id, Function<FriendlyByteBuf, T> decode,
                                                                     Packet.Handle<T> handler) {
            ClientPlayNetworking.registerGlobalReceiver(id, (client, listener, buf, responseSender) -> {
                buf.retain();
                client.execute(() -> {
                    T packet = decode.apply(buf);
                    ClientLevel level = client.level;
                    if (level != null) {
                        try {
                            handler.handle(packet, level, Minecraft.getInstance().player);
                        } catch (Throwable throwable) {
                            ExampleMod.LOGGER.error("Packet \"%s\" failed: ".formatted(id.toString()), throwable);
                            throw throwable;
                        }
                    }
                    buf.release();
                });
            });
        }
    }

    public static class ServerProxy {
        private static <T extends Packet> void registerServerReceiver(ResourceLocation id, Function<FriendlyByteBuf, T> decode, Packet.Handle<T> handler) {
            ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler1, buf, responseSender) -> {
                buf.retain();
                server.execute(() -> {
                    T packet = decode.apply(buf);
                    Level level = player.level();
                    try {
                        handler.handle(packet, level, player);
                    } catch (Throwable throwable) {
                        ExampleMod.LOGGER.error("Packet \"%s\" failed: ".formatted(id.toString()), throwable);
                        throw throwable;
                    }
                    buf.release();
                });
            });
        }
    }
}