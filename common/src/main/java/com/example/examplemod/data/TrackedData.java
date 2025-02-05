package com.example.examplemod.data;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface TrackedData<T> extends Supplier<T> {

    @Nullable
    CompoundTag save();

    void load(CompoundTag tag);
}
