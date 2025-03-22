package dev.corgitaco.dataanchor.levelgen;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData;
import dev.corgitaco.dataanchor.data.type.chunk.ProtoChunkTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.jetbrains.annotations.Nullable;

public class CanyonChunkTrackedData extends ProtoChunkTrackedData {

    public static final TrackedDataKey<CanyonChunkTrackedData> KEY = TrackedDataRegistries.CHUNK.register(ResourceLocation.fromNamespaceAndPath(DataAnchor.MOD_ID, "canyon_chunk"), CanyonChunkTrackedData.class,
            (key, obj) -> obj instanceof ProtoChunk protoChunk && !(obj instanceof ImposterProtoChunk) ? new CanyonChunkTrackedData(key, protoChunk) : null
    );

    public CanyonChunkTrackedData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, ProtoChunk chunk) {
        super(trackedDataKey, chunk);
    }

    @Override
    public void load(CompoundTag tag) {
    }

    @Override
    public @Nullable CompoundTag save() {
        if (chunk.levelHeightAccessor instanceof ServerLevelAccessor serverLevelAccessor) {
            TrackedDataRegistries.LEVEL.get(CanyonLevelTrackedData.KEY, serverLevelAccessor.getLevel()).ifPresent(canyonLevelTrackedData -> {
                canyonLevelTrackedData.getCanyonStorage().worldGenChunkUnload(get());
            });
        }

        return null;
    }

    public void worldGenChunkLoad(ProtoChunk chunk) {
        if (chunk.levelHeightAccessor instanceof ServerLevelAccessor serverLevelAccessor) {
            TrackedDataRegistries.LEVEL.get(CanyonLevelTrackedData.KEY, serverLevelAccessor.getLevel()).ifPresent(canyonLevelTrackedData -> {
                canyonLevelTrackedData.getCanyonStorage().worldGenChunkLoad(chunk);
            });
        }
    }

    public void afterSurface(ProtoChunk chunk, WorldGenRegion context) {
        worldGenChunkLoad(chunk);
        if (chunk.levelHeightAccessor instanceof ServerLevelAccessor serverLevelAccessor) {
            TrackedDataRegistries.LEVEL.get(CanyonLevelTrackedData.KEY, serverLevelAccessor.getLevel()).ifPresent(canyonLevelTrackedData -> {
                canyonLevelTrackedData.getCanyonStorage().afterSurface(chunk, context);
                canyonLevelTrackedData.getChunkRipper().onChunkLoad(chunk);
            });
        }
    }

}
