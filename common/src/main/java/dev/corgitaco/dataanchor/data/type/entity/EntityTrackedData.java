/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.data.type.entity;

import dev.corgitaco.dataanchor.data.TrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.entity.Entity;

public abstract sealed class EntityTrackedData implements TrackedData<Entity> permits ServerEntityTrackedData, SyncedEntityTrackedData, PlayerTrackedData {

    protected transient final TrackedDataKey<? extends EntityTrackedData> trackedDataKey;
    protected transient final Entity entity;

    public EntityTrackedData(TrackedDataKey<? extends EntityTrackedData> trackedDataKey, Entity entity) {
        this.trackedDataKey = trackedDataKey;
        this.entity = entity;
    }

    public void addDuringPortalTeleport() {
        // Do something when the entity is added to the world from a portal teleport
    }

    @Override
    public Entity get() {
        return this.entity;
    }
}
