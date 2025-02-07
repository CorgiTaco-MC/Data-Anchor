package com.example.examplemod.data.type.level.network;

import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.registry.TrackedDataRegistries;
import com.example.examplemod.data.type.level.SyncedLevelTrackedData;
import com.example.examplemod.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record SyncLevelTrackedDataS2C(TrackedDataKey<SyncedLevelTrackedData> dataKey, CompoundTag tag) implements Packet {


    public SyncLevelTrackedDataS2C(FriendlyByteBuf buf) {
        this((TrackedDataKey) TrackedDataKey.fromID(TrackedDataRegistries.LEVEL, buf.readResourceLocation()), buf.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(dataKey.getId());
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        if (level != null) {
            if (level instanceof TrackedDataContainer access) {
                access.get(this.dataKey).ifPresent(data -> {
                    if (data instanceof SyncedLevelTrackedData syncedData) {
                        syncedData.readFromNetwork(tag);
                    }
                });
            }
        }
    }
}
