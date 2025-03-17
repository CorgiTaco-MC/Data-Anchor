package dev.corgitaco.dataanchor.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface Packet extends CustomPacketPayload {

    void handle(@Nullable Level level, @Nullable Player player);

    record Handler<T extends Packet>(Class<T> clazz, Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> serializer, Handle<T> handle) {
    }


    @FunctionalInterface
    interface Handle<T extends Packet> {
        void handle(T packet, Level level, Player player);
    }
}
