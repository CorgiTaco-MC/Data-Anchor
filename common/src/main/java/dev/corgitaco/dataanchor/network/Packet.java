/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public interface Packet extends CustomPacketPayload {

    void handle(@Nullable Level level, @Nullable Player player);

    default void write(FriendlyByteBuf buf) {
    }

    record Handler<T extends Packet>(Class<T> clazz, Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> serializer,
                                     Handle<T> handle) {

        public Handler(Class<T> clazz) {
            this(clazz, getType(clazz), makeCodec(clazz), Packet::handle);
        }
    }

    private static <T extends Packet> StreamCodec<RegistryFriendlyByteBuf, T> makeCodec(Class<T> clazz) {
        StreamDecoder<RegistryFriendlyByteBuf, T> packetRead = friendlyByteBuf -> {
            try {
                return clazz.getConstructor(FriendlyByteBuf.class).newInstance(friendlyByteBuf);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException("Packet class missing constructor method with single `FriendlyByteBuf` argument.", e);
            }
        };


        return CustomPacketPayload.codec(Packet::write, packetRead);

    }

    private static <T extends Packet> Type<T> getType(Class<T> clazz) {
        try {
            Field foundTypeField = null;
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (Type.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    if (foundTypeField != null) {
                        throw new RuntimeException("Multiple CustomPacketPayload.Type static fields found for " + clazz.getName() + ". Reference the type field directly with another constructor.");
                    }
                    foundTypeField = field;
                }
            }

            if (foundTypeField != null) {
                @SuppressWarnings("unchecked")
                Type<T> type = (Type<T>) foundTypeField.get(null);
                return type;
            }
            throw new RuntimeException("No CustomPacketPayload.Type static field found for " + clazz.getName() + ". Reference the type field directly with another constructor.");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get packet type for " + clazz.getName(), e);
        }
    }


    @FunctionalInterface
    interface Handle<T extends Packet> {
        void handle(T packet, Level level, Player player);
    }
}
