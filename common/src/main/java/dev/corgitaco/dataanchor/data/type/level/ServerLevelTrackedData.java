/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.level;

import dev.corgitaco.dataanchor.data.DirtyMarker;
import dev.corgitaco.dataanchor.data.InternalDirtyMarker;
import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.server.level.ServerLevel;

public abstract non-sealed class ServerLevelTrackedData extends LevelTrackedData implements DirtyMarker, ServerTrackedData {

    public ServerLevelTrackedData(TrackedDataKey<? extends ServerLevelTrackedData> trackedDataKey, ServerLevel chunk) {
        super(trackedDataKey, chunk);
    }

    @Override
    public ServerLevel get() {
        return (ServerLevel) super.get();
    }

    @Override
    public void markDirty() {
        if (!level.isClientSide) {
            if (level instanceof InternalDirtyMarker dirtyMarker) {
                dirtyMarker.dataAnchor$markDirty();
            }
        }
    }

    @Override
    public void clearDirty() {
    }
}
