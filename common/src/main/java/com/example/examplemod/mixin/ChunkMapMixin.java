package com.example.examplemod.mixin;

import com.example.examplemod.data.SyncedTrackedData;
import com.example.examplemod.data.TrackedData;
import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.TrackedDataKey;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    @Inject(method = "playerLoadedChunk", at = @At("RETURN"))
    private void onPlayerLoadedChunk(ServerPlayer player, MutableObject<ClientboundLevelChunkWithLightPacket> packetCache, LevelChunk chunk, CallbackInfo ci) {
        if (chunk instanceof TrackedDataContainer<?, ?> trackedDataContainer) {
            for (TrackedDataKey key : trackedDataContainer.getKeys()) {
                TrackedData trackedData = trackedDataContainer.get(key);
                if (trackedData instanceof SyncedTrackedData syncedData) {
                    syncedData.syncToPlayer(player);
                }
            }
        }
    }
}
