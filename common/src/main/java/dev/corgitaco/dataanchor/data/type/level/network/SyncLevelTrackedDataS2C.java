/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.level.network;

import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.level.SyncedLevelTrackedData;
import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record SyncLevelTrackedDataS2C(TrackedDataKey<SyncedLevelTrackedData> dataKey, CompoundTag tag) implements Packet {


    public SyncLevelTrackedDataS2C(FriendlyByteBuf buf) {
        this((TrackedDataKey) TrackedDataKey.fromID(TrackedDataRegistries.LEVEL, buf.readResourceLocation()), buf.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(dataKey.getId());
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        if (level != null) {
            if (level instanceof TrackedDataContainer access) {
                access.dataAnchor$getTrackedData(this.dataKey).ifPresent(data -> {
                    if (data instanceof SyncedLevelTrackedData syncedData) {
                        syncedData.readFromNetwork(tag);
                    }
                });
            }
        }
    }
}
