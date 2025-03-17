package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {


    @Inject(method = "write", at = @At("RETURN"))
    private static void dataAnchor$write(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir) {
        if (chunk instanceof TrackedDataContainer trackedDataContainer) {
            CompoundTag tag = cir.getReturnValue();

            CompoundTag trackedDataTag = new CompoundTag();
            Collection<TrackedDataKey<ChunkTrackedData>> keys = trackedDataContainer.dataAnchor$getTrackedDataKeys();
            for (TrackedDataKey<ChunkTrackedData> key : keys) {
                trackedDataContainer.dataAnchor$getTrackedData(key).ifPresent(trackedData -> {
                    if (trackedData instanceof ChunkTrackedData chunkTrackedData) {
                        CompoundTag save = chunkTrackedData.save();
                        if (save != null) {
                            trackedDataTag.put(key.getId().toString(), save);
                        }
                    }
                });
            }

            tag.put("TrackedData", trackedDataTag);
        }
    }

    @Inject(method = "read", at = @At("RETURN"))
    private static void dataAnchor$read(ServerLevel level, PoiManager poiManager, RegionStorageInfo regionStorageInfo, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir) {
        ChunkAccess returnValue = cir.getReturnValue();
        if (returnValue instanceof ImposterProtoChunk imposterProtoChunk) {
            returnValue = imposterProtoChunk.getWrapped();
        }

        if (returnValue instanceof TrackedDataContainer<?, ?> trackedDataContainer) {
            CompoundTag trackedDataTag = tag.getCompound("TrackedData");
            for (TrackedDataKey key : trackedDataContainer.dataAnchor$getTrackedDataKeys()) {
                trackedDataContainer.dataAnchor$getTrackedData(key).ifPresent(trackedData -> {
                    if (trackedData instanceof ChunkTrackedData chunkTrackedData) {
                        String idString = key.getId().toString();
                        if (trackedDataTag.contains(idString)) {
                            chunkTrackedData.load(trackedDataTag.getCompound(idString));
                        }
                    }
                });
            }
        }
    }
}
