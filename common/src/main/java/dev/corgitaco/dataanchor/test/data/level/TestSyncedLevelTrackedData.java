package dev.corgitaco.dataanchor.test.data.level;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.level.SyncedLevelTrackedData;
import net.minecraft.world.level.Level;

public class TestSyncedLevelTrackedData extends SyncedLevelTrackedData implements TickableTrackedData {

    private int yum = -1000;

    public TestSyncedLevelTrackedData(TrackedDataKey<? extends SyncedLevelTrackedData> trackedDataKey, Level level) {
        super(trackedDataKey, level);
    }

    @Override
    public void tick() {
        if (level.dimension() == Level.OVERWORLD) {
            if (!level.isClientSide) {
                setYum(this.yum + 1);

                DataAnchor.LOGGER.info("Server level yum: %s".formatted(this.yum));
            } else {
                DataAnchor.LOGGER.info("Client level yum: %s".formatted(this.yum));
            }
        }
    }

    public void setYum(int yum) {
        this.yum = yum;
        sync();
        dataAnchor$markDirty();
    }
}
