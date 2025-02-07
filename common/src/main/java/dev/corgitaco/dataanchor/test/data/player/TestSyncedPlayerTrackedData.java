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
    public @Nullable CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("yum", yum);
        return compoundTag;
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("yum")) {
            setYum(tag.getInt("yum"));
        }
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
