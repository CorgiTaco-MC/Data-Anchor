package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.type.chunk.ChunkTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {


    @Inject(method = "write", at = @At("RETURN"))
    private static void save(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir) {
        if (chunk instanceof TrackedDataContainer trackedDataContainer) {
            CompoundTag tag = cir.getReturnValue();

            CompoundTag trackedDataTag = new CompoundTag();
            Collection<TrackedDataKey<ChunkTrackedData>> keys = trackedDataContainer.getKeys();
            for (TrackedDataKey<ChunkTrackedData> key : keys) {
                trackedDataContainer.get(key).ifPresent(trackedData -> {
                    if (trackedData instanceof ChunkTrackedData chunkTrackedData) {
                        trackedDataTag.put(key.getId().toString(), chunkTrackedData.save());
                    }
                });
            }

            tag.put("TrackedData", trackedDataTag);
        }
    }

    @Inject(method = "read", at = @At("RETURN"))
    private static void load(ServerLevel level, PoiManager poiManager, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir) {
        ChunkAccess returnValue = cir.getReturnValue();
        if (returnValue instanceof ImposterProtoChunk imposterProtoChunk) {
            returnValue = imposterProtoChunk.getWrapped();
        }

        if (returnValue instanceof TrackedDataContainer<?, ?> trackedDataContainer) {
            CompoundTag trackedDataTag = tag.getCompound("TrackedData");
            for (TrackedDataKey key : trackedDataContainer.getKeys()) {
                trackedDataContainer.get(key).ifPresent(trackedData -> {
                    if (trackedData instanceof ChunkTrackedData chunkTrackedData) {
                        chunkTrackedData.load(trackedDataTag.getCompound(key.getId().toString()));
                    }
                });
            }
        }
    }
}
