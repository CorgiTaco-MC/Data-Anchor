package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
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
                trackedDataContainer.get(key).ifPresent(trackedData -> {
                    if (trackedData instanceof SyncedTrackedData syncedData) {
                        syncedData.syncToPlayer(player);
                    }
                });
            }

            chunk.getBlockEntities().values().forEach(entry -> {
                if (entry instanceof TrackedDataContainer<?, ?> blockEntityContainer) {
                    for (TrackedDataKey key : blockEntityContainer.getKeys()) {
                        blockEntityContainer.get(key).ifPresent(blockEntityData -> {
                            if (blockEntityData instanceof SyncedTrackedData syncedBlockEntityData) {
                                syncedBlockEntityData.syncToPlayer(player);
                            }
                        });
                    }
                }
            });
        }
    }
}
