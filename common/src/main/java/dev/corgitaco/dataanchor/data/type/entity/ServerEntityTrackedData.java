package dev.corgitaco.dataanchor.data.type.entity;

import dev.corgitaco.dataanchor.data.ServerTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import net.minecraft.world.entity.Entity;

public abstract non-sealed class ServerEntityTrackedData extends EntityTrackedData implements ServerTrackedData {

    public ServerEntityTrackedData(TrackedDataKey<? extends EntityTrackedData> trackedDataKey, Entity entity) {
        super(trackedDataKey, entity);
    }
}
