package dev.corgitaco.dataanchor.data.type.entity.network;

import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.entity.EntityTrackedData;
import dev.corgitaco.dataanchor.network.Packet;
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
