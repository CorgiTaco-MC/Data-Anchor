package dev.corgitaco.dataanchor.fabric.network;

import dev.corgitaco.dataanchor.network.Packet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class FabricPacketBroadcaster {

    protected final <T extends Packet> void register(Packet.Handler<T> handler) {
        registerPayload(handler.type(), handler.serializer());
        registerHandler(handler);
    }

    protected abstract <T extends Packet> void registerPayload(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> serializer);

    protected abstract <T extends Packet> void registerHandler(Packet.Handler<T> handler);

    public record ClientProxy() {
        public static <T extends Packet> void registerClientReceiver(Packet.Handler<T> handler) {
            ClientPlayNetworking.registerGlobalReceiver(handler.type(), (t, context) -> handler.handle().handle(t, context.client().level, context.player()));
        }
    }

    public static class ServerProxy {
        static <T extends Packet> void registerServerReceiver(Packet.Handler<T> handler) {
            ServerPlayNetworking.registerGlobalReceiver(handler.type(), (t, context) -> handler.handle().handle(t, context.player().level(), context.player()));
        }
    }
}