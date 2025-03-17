package dev.corgitaco.dataanchor.forge.network;

import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.payload.PayloadConnection;
import net.minecraftforge.network.payload.PayloadFlow;
import net.minecraftforge.network.payload.PayloadProtocol;
import net.minecraftforge.network.simple.BaseProtocol;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class ForgeNetworkHandler {

    protected final Map<Class<? extends Packet>, Channel<CustomPacketPayload>> channels = new ConcurrentHashMap<>();
    private final NetworkDirection networkDirection;

    public ForgeNetworkHandler(NetworkDirection networkDirection) {
        this.networkDirection = networkDirection;
    }

    public <T extends Packet> void registerMessage(Packet.Handler<T> handler) {
        channels.computeIfAbsent(handler.clazz(), aClass -> {
            PayloadConnection<CustomPacketPayload> connection = ChannelBuilder.named(handler.type().id()).payloadChannel();
            PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> play = this.networkDirection.protocolFunction.apply(connection.play());
            this.networkDirection.registerHandles((PayloadFlow) play, handler);
            return ((PayloadFlow) play).build();
        });
    }

    private static class Client {
        private static <T extends Packet> void clientHandle(T packet, Packet.Handle<T> handle) {
            handle.handle(packet, Minecraft.getInstance().level, Minecraft.getInstance().player);
        }
    }

    public enum NetworkDirection {
        C2S(BaseProtocol::serverbound),
        S2C(BaseProtocol::clientbound),
        BIDIRECTIONAL(BaseProtocol::bidirectional);

        private final Function<PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload>, PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload>> protocolFunction;

        NetworkDirection(Function<PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload>, PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload>> protocolFunction) {
            this.protocolFunction = protocolFunction;
        }

        private <T extends Packet> void registerHandles(PayloadFlow<RegistryFriendlyByteBuf, T> flow, Packet.Handler<T> handler) {
            switch (this) {
                case S2C -> registerS2C(handler, flow);
                case C2S -> registerC2S(handler, flow);
                case BIDIRECTIONAL -> registerBiDirectional(handler, flow);
            }
        }

        private static <T extends Packet> void registerS2C(Packet.Handler<T> handler, PayloadFlow<RegistryFriendlyByteBuf, T> flow) {
            flow.addMain(handler.type(), handler.serializer(), (t, context) -> {
                context.enqueueWork(() -> handler.handle().handle(t, context.getSender().level(), context.getSender()));
                context.setPacketHandled(true);
            });
        }

        private static <T extends Packet> void registerC2S(Packet.Handler<T> handler, PayloadFlow<RegistryFriendlyByteBuf, T> flow) {
            flow.addMain(handler.type(), handler.serializer(), (t, context) -> {
                context.enqueueWork(() -> Client.clientHandle(t, handler.handle()));
                context.setPacketHandled(true);
            });
        }

        private static <T extends Packet> void registerBiDirectional(Packet.Handler<T> handler, PayloadFlow<RegistryFriendlyByteBuf, T> flow) {
            flow.addMain(handler.type(), handler.serializer(), (t, context) -> {
                if (FMLLoader.getDist() == Dist.CLIENT) {
                    if (context.isClientSide()) {
                        Client.clientHandle(t, handler.handle());
                    }
                } else {
                    handler.handle().handle(t, context.getSender().level(), context.getSender());
                }
                context.setPacketHandled(true);
            });
        }
    }
}