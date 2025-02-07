package com.example.examplemod.data.type.entity;

import com.example.examplemod.data.ServerTrackedData;
import com.example.examplemod.data.registry.TrackedDataKey;
import net.minecraft.world.entity.Entity;

public abstract non-sealed class ServerEntityTrackedData extends EntityTrackedData implements ServerTrackedData {

    public ServerEntityTrackedData(TrackedDataKey<? extends EntityTrackedData> trackedDataKey, Entity entity) {
        super(trackedDataKey, entity);
    }
}
