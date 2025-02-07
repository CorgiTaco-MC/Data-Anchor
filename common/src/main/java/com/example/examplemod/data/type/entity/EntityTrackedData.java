package com.example.examplemod.data.type.entity;

import com.example.examplemod.data.TrackedData;
import com.example.examplemod.data.registry.TrackedDataKey;
import net.minecraft.world.entity.Entity;

public abstract sealed class EntityTrackedData implements TrackedData<Entity> permits ServerEntityTrackedData, SyncedEntityTrackedData, PlayerTrackedData {

    protected final TrackedDataKey<? extends EntityTrackedData> trackedDataKey;
    protected final Entity entity;

    public EntityTrackedData(TrackedDataKey<? extends EntityTrackedData> trackedDataKey, Entity entity) {
        this.trackedDataKey = trackedDataKey;
        this.entity = entity;
    }

    @Override
    public Entity get() {
        return this.entity;
    }
}
