/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.test.data.chunk;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData;
import dev.corgitaco.dataanchor.data.type.chunk.SyncedLevelChunkTrackedData;
import net.minecraft.world.level.chunk.LevelChunk;

public class TestSyncedLevelChunkTrackedData extends SyncedLevelChunkTrackedData implements TickableTrackedData {
    int timer = 0;


    public TestSyncedLevelChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, LevelChunk chunk) {
        super(trackedDataKey, chunk);
    }

    @Override
    public void tick() {
        if (get().getPos().x() == 0 && get().getPos().z() == 0) {
            if (!get().getLevel().isClientSide()) {
                setTimer(this.timer + 1);
                DataAnchor.LOGGER.info("Server chunk timer: %s".formatted(this.timer));
            } else {
                DataAnchor.LOGGER.info("Client chunk timer: %s".formatted(this.timer));
            }
        }


    }

    public void setTimer(int timer) {
        this.timer = timer;
        sync();
        markDirty();
    }
}
