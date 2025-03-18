/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public interface SyncedTrackedData extends ServerTrackedData, ClientTrackedData {

    void sync();

    void syncToPlayer(ServerPlayer player);

    default void readFromNetwork(CompoundTag tag) {
        if (this instanceof TrackedData trackedData) {
            trackedData.load(tag);
        }
    }

    default CompoundTag writeToNetwork() {
        if (this instanceof TrackedData trackedData) {
           return trackedData.save();
        }

        return new CompoundTag();
    }
}
