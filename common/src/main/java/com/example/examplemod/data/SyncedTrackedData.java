package com.example.examplemod.data;

import net.minecraft.nbt.CompoundTag;

public interface SyncedTrackedData {

    void sync();

    default void readFromNetwork(CompoundTag tag) {
        if (this instanceof TrackedData trackedData) {
            trackedData.load(tag);
        }
    }

    default CompoundTag writeToNetwork() {
        if (this instanceof TrackedData trackedData) {
           return trackedData.save();
        }

        return new CompoundTag();
    }
}
