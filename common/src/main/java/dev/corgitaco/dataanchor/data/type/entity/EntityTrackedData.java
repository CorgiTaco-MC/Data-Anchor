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

    @Override
    public Entity get() {
        return this.entity;
    }
}
