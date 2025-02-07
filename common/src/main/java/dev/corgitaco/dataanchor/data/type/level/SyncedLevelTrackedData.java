package dev.corgitaco.dataanchor.data.type.level;

import dev.corgitaco.dataanchor.data.DirtyMarker;
import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.level.network.SyncLevelTrackedDataS2C;
import dev.corgitaco.dataanchor.network.broadcast.S2CPacketBroadcaster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public abstract non-sealed class SyncedLevelTrackedData extends LevelTrackedData implements SyncedTrackedData, DirtyMarker {


    public SyncedLevelTrackedData(TrackedDataKey<? extends SyncedLevelTrackedData> trackedDataKey, Level level) {
        super(trackedDataKey, level);
    }

    @Override
    public void sync() {
        if (!level.isClientSide) {
            S2CPacketBroadcaster.S2C.sendToAllPlayersInDimension(new SyncLevelTrackedDataS2C((TrackedDataKey<SyncedLevelTrackedData>) trackedDataKey, writeToNetwork()), get().dimension());

            markDirty();
        }
    }

    @Override
    public void syncToPlayer(ServerPlayer player) {
        S2CPacketBroadcaster.S2C.sendToPlayer(new SyncLevelTrackedDataS2C((TrackedDataKey<SyncedLevelTrackedData>) trackedDataKey, writeToNetwork()), player);
    }

    @Override
    public void markDirty() {
        if (!level.isClientSide) {
            if (level instanceof DirtyMarker dirtyMarker) {
                dirtyMarker.markDirty();
            }
        }
    }

    @Override
    public void clearDirty() {
    }
}
