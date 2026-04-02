/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.entity.network;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.entity.EntityTrackedData;
import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record SyncEntityTrackedDataS2C(int id, TrackedDataKey<? extends EntityTrackedData> dataKey,
                                       CompoundTag tag) implements Packet {

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncEntityTrackedDataS2C> STREAM_CODEC = CustomPacketPayload.codec(SyncEntityTrackedDataS2C::write, SyncEntityTrackedDataS2C::new);
    public static final Type<SyncEntityTrackedDataS2C> TYPE = new Type<>(DataAnchor.id("entity_tracked_data"));

    public SyncEntityTrackedDataS2C(FriendlyByteBuf buf) {
        this(buf.readInt(), TrackedDataKey.fromID(TrackedDataRegistries.ENTITY, buf.readIdentifier()), buf.readNbt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeIdentifier(dataKey.getId());
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        if (level.getEntity(this.id) instanceof TrackedDataContainer access) {
            access.dataAnchor$getTrackedData(this.dataKey).ifPresent(data -> {
                if (data instanceof SyncedTrackedData syncedData) {
                    syncedData.readFromNetwork(tag);
                }
            });
        } else {
            DataAnchor.LOGGER.warn("Failed to sync entity tracked data {}: Entity {} not found", this.dataKey.getId().toString(), this.id);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
