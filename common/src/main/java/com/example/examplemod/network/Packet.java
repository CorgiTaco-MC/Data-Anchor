package com.example.examplemod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface Packet {

    void write(FriendlyByteBuf buf);

    void handle(@Nullable Level level, @Nullable Player player);

    record Handler<T extends Packet>(Class<T> clazz, BiConsumer<T, FriendlyByteBuf> write,
                                     Function<FriendlyByteBuf, T> read,
                                     Handle<T> handle) {
    }


    @FunctionalInterface
    interface Handle<T extends Packet> {
        void handle(T packet, Level level, Player player);
    }
}
