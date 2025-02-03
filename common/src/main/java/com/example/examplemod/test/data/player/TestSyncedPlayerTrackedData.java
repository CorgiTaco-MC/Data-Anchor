package com.example.examplemod.test.data.player;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.data.TickableTrackedData;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.SyncedPlayerTrackedData;
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

            ExampleMod.LOGGER.info("Server yum: %s".formatted(this.yum));
        } else {
            ExampleMod.LOGGER.info("Client yum: %s".formatted(this.yum));
        }
    }

   public void setYum(int yum) {
        this.yum = yum;
        sync();
    }
}
