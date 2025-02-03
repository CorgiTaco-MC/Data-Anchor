package com.example.examplemod.data;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface TrackedData {

    @Nullable
    CompoundTag save();

    void load(CompoundTag tag);
}
