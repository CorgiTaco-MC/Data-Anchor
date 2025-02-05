package com.example.examplemod.test.data.level;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.data.TickableTrackedData;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.level.LevelTrackedData;
import com.example.examplemod.data.level.SyncedLevelTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TestSyncedLevelTrackedData extends SyncedLevelTrackedData implements TickableTrackedData {

    private int yum = -1000;

    public TestSyncedLevelTrackedData(TrackedDataKey<? extends SyncedLevelTrackedData> trackedDataKey, Level level) {
        super(trackedDataKey, level);
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
        if (!level.isClientSide) {
            setYum(this.yum + 1);

            ExampleMod.LOGGER.info("Server level yum: %s".formatted(this.yum));
        } else {
            ExampleMod.LOGGER.info("Client level yum: %s".formatted(this.yum));
        }
    }

    public void setYum(int yum) {
        this.yum = yum;
        sync();
        markDirty();
    }
}
