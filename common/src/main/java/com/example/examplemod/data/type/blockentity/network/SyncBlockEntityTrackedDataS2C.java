package com.example.examplemod.data.type.blockentity.network;

import com.example.examplemod.data.SyncedTrackedData;
import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.registry.TrackedDataRegistries;
import com.example.examplemod.data.type.blockentity.BlockEntityTrackedData;
import com.example.examplemod.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record SyncBlockEntityTrackedDataS2C(BlockPos pos, TrackedDataKey<? extends BlockEntityTrackedData> dataKey,
                                            CompoundTag tag) implements Packet {

    public SyncBlockEntityTrackedDataS2C(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), TrackedDataKey.fromID(TrackedDataRegistries.BLOCK_ENTITY, buf.readResourceLocation()), buf.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeResourceLocation(dataKey.getId());
        buf.writeNbt(this.tag);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        if (level.getBlockEntity(this.pos) instanceof TrackedDataContainer access) {
            access.get(this.dataKey).ifPresent(data -> {
                if (data instanceof SyncedTrackedData syncedData) {
                    syncedData.readFromNetwork(tag);
                }
            });
        }
    }
}
