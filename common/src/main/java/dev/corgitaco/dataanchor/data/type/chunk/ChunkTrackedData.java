/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.chunk;

import dev.corgitaco.dataanchor.data.DirtyMarker;
import dev.corgitaco.dataanchor.data.TrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.level.chunk.ChunkAccess;

public abstract sealed class ChunkTrackedData implements TrackedData<ChunkAccess>, DirtyMarker permits LevelChunkTrackedData, ProtoChunkTrackedData {

    protected transient final TrackedDataKey<? extends ChunkTrackedData> trackedDataKey;
    protected transient final ChunkAccess chunk;

    public ChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, ChunkAccess chunk) {
        this.trackedDataKey = trackedDataKey;
        this.chunk = chunk;
    }

    @Override
    public ChunkAccess get() {
        return this.chunk;
    }

    @Override
    public void markDirty() {
        chunk.setUnsaved(true);
    }

    @Override
    public void clearDirty() {

    }
}
