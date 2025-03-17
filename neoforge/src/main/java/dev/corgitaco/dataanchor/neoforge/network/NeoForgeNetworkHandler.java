package dev.corgitaco.dataanchor.neoforge.network;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.network.BiDirectionalNetworkContainer;
import dev.corgitaco.dataanchor.network.C2SNetworkContainer;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.S2CNetworkContainer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = DataAnchor.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NeoForgeNetworkHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        C2SNetworkContainer.C2S_NAMESPACED_CONTAINERS.forEach((modid, containers) -> containers.registerMessages(handler -> registerC2S(handler, registrar)));
        S2CNetworkContainer.S2C_NAMESPACED_CONTAINERS.forEach((modid, containers) -> containers.registerMessages(handler -> registerS2C(handler, registrar)));
        BiDirectionalNetworkContainer.BI_NAMESPACED_CONTAINERS.forEach((modid, containers) -> containers.registerMessages(handler -> registerBidirectional(handler, registrar)));
    }

    private static <T extends Packet> void registerC2S(Packet.Handler<T> handler, PayloadRegistrar registrar) {
        registrar.playToServer(handler.type(), handler.serializer(), (arg, iPayloadContext) -> arg.handle(iPayloadContext.player().level(), iPayloadContext.player()));
    }

    private static <T extends Packet> void registerS2C(Packet.Handler<T> handler, PayloadRegistrar registrar) {
        registrar.playToClient(handler.type(), handler.serializer(), (arg, iPayloadContext) -> arg.handle(iPayloadContext.player().level(), iPayloadContext.player()));
    }

    private static <T extends Packet> void registerBidirectional(Packet.Handler<T> handler, PayloadRegistrar registrar) {
        registrar.playBidirectional(handler.type(), handler.serializer(), (arg, iPayloadContext) -> arg.handle(iPayloadContext.player().level(), iPayloadContext.player()));
    }
}