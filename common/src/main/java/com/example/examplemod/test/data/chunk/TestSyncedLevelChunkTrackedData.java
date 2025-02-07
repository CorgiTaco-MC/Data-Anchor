package com.example.examplemod.test.data.chunk;

import com.example.examplemod.data.TickableTrackedData;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.type.chunk.ChunkTrackedData;
import com.example.examplemod.data.type.chunk.SyncedLevelChunkTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public class TestSyncedLevelChunkTrackedData extends SyncedLevelChunkTrackedData implements TickableTrackedData {
    int timer = 0;


    public TestSyncedLevelChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, LevelChunk chunk) {
        super(trackedDataKey, chunk);
    }

    @Override
    public @Nullable CompoundTag save() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("timer", timer);
        return compoundTag;
    }

    @Override
    public void load(CompoundTag tag) {
        this.timer = tag.getInt("timer");
    }

    @Override
    public void tick() {
        if (!get().getLevel().isClientSide && get().getPos().x == 0 && get().getPos().z == 0) {
            setTimer(this.timer + 1);
        }
    }

    public void setTimer(int timer) {
        this.timer = timer;
        sync();
        markDirty();
    }
}
