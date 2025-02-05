package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedData;
import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.TrackedDataKey;
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

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {


    @Inject(method = "write", at = @At("RETURN"))
    private static void save(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<CompoundTag> cir) {
        if (chunk instanceof TrackedDataContainer<?,?> trackedDataContainer) {
            CompoundTag tag = cir.getReturnValue();

            CompoundTag trackedDataTag = new CompoundTag();
            for (TrackedDataKey key : trackedDataContainer.getKeys()) {
                TrackedData trackedData = trackedDataContainer.get(key);
                if (trackedData == null) {
                    continue;
                }
                trackedDataTag.put(key.getId().toString(), trackedData.save());
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

        if (returnValue instanceof TrackedDataContainer<?,?> trackedDataContainer) {
            CompoundTag trackedDataTag = tag.getCompound("TrackedData");
            for (TrackedDataKey key : trackedDataContainer.getKeys()) {
                TrackedData trackedData = trackedDataContainer.get(key);
                if (trackedData == null) {
                    continue;
                }
                trackedData.load(trackedDataTag.getCompound(key.getId().toString()));
            }
        }
    }
}
