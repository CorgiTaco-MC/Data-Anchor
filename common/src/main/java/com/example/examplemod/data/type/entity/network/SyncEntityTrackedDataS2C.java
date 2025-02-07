package com.example.examplemod.data.type.entity.network;

import com.example.examplemod.data.SyncedTrackedData;
import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.registry.TrackedDataRegistries;
import com.example.examplemod.data.type.entity.EntityTrackedData;
import com.example.examplemod.network.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record SyncEntityTrackedDataS2C(int id, TrackedDataKey<? extends EntityTrackedData> dataKey,
                                       CompoundTag tag) implements Packet {

    public SyncEntityTrackedDataS2C(FriendlyByteBuf buf) {
        this(buf.readInt(), TrackedDataKey.fromID(TrackedDataRegistries.ENTITY, buf.readResourceLocation()), buf.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeResourceLocation(dataKey.getId());
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        if (level.getEntity(this.id) instanceof TrackedDataContainer access) {
            access.get(this.dataKey).ifPresent(data -> {
                if (data instanceof SyncedTrackedData syncedData) {
                    syncedData.readFromNetwork(tag);
                }
            });
        }
    }
}
