/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.chunk;

import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.level.chunk.LevelChunk;

public abstract non-sealed class ServerLevelChunkTrackedData extends LevelChunkTrackedData implements ServerTrackedData {

    public ServerLevelChunkTrackedData(TrackedDataKey<? extends LevelChunkTrackedData> trackedDataKey, LevelChunk levelChunk) {
        super(trackedDataKey, levelChunk);
    }
}
