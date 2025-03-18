/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.level;

import dev.corgitaco.dataanchor.data.TrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.level.Level;

public abstract sealed class LevelTrackedData implements TrackedData<Level> permits ServerLevelTrackedData, SyncedLevelTrackedData {

    protected transient final TrackedDataKey<? extends LevelTrackedData> trackedDataKey;
    protected transient final Level level;

    public LevelTrackedData(TrackedDataKey<? extends LevelTrackedData> trackedDataKey, Level level) {
        this.trackedDataKey = trackedDataKey;
        this.level = level;
    }

    @Override
    public Level get() {
        return this.level;
    }
}
