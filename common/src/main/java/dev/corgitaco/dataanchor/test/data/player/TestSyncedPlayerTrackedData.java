package dev.corgitaco.dataanchor.test.data.player;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.entity.SyncedPlayerTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class TestSyncedPlayerTrackedData extends SyncedPlayerTrackedData implements TickableTrackedData {

    private int yum = 0;

    public TestSyncedPlayerTrackedData(TrackedDataKey<? extends SyncedPlayerTrackedData> trackedDataKey, Player player) {
        super(trackedDataKey, player);
    }

    @Override
    public void tick() {
        if (!player.level().isClientSide) {
            setYum(this.yum + 1);

            DataAnchor.LOGGER.info("Server player yum: %s".formatted(this.yum));
        } else {
            DataAnchor.LOGGER.info("Client player yum: %s".formatted(this.yum));
        }
    }

   public void setYum(int yum) {
        this.yum = yum;
        sync();

    }
}
