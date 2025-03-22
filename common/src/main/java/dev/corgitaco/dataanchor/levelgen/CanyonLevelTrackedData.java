package dev.corgitaco.dataanchor.levelgen;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.level.ServerLevelTrackedData;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CanyonLevelTrackedData extends ServerLevelTrackedData {
    public static final TrackedDataKey<CanyonLevelTrackedData> KEY = TrackedDataRegistries.LEVEL.register(ResourceLocation.fromNamespaceAndPath(DataAnchor.MOD_ID, "canyon"), CanyonLevelTrackedData.class,
            (key, obj) -> obj instanceof ServerLevel serverLevel && serverLevel.dimension() == Level.OVERWORLD ? new CanyonLevelTrackedData(key, serverLevel) : null
    );

    private final CanyonStorage canyonStorage = new CanyonStorage();

    private final ChunkRipper chunkRipper;




    public CanyonLevelTrackedData(TrackedDataKey<? extends ServerLevelTrackedData> trackedDataKey, ServerLevel serverLevel) {
        super(trackedDataKey, serverLevel);
        this.chunkRipper = new ChunkRipper(serverLevel);
    }

    @Override
    public @Nullable CompoundTag save() {
        return null;
    }

    @Override
    public void load(CompoundTag tag) {
    }

    public CanyonStorage getCanyonStorage() {
        return canyonStorage;
    }

    public ChunkRipper getChunkRipper() {
        return chunkRipper;
    }
}
