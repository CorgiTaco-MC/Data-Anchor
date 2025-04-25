/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData;
import dev.corgitaco.dataanchor.data.type.entity.PlayerTrackedData;
import dev.corgitaco.dataanchor.data.type.entity.SyncedPlayerTrackedData;
import dev.corgitaco.dataanchor.data.type.level.LevelTrackedData;
import dev.corgitaco.dataanchor.data.type.level.SyncedLevelTrackedData;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {


    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickChunk", at = @At("RETURN"))
    private void dataAnchor$onTickChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        if (chunk instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<ChunkTrackedData>> keys = access.dataAnchor$getTrackedDataKeys();
            for (TrackedDataKey<ChunkTrackedData> key : keys) {
                access.dataAnchor$getTrackedData(key).ifPresent(data -> {
                    if (data instanceof TickableTrackedData tickableData) {
                        tickableData.tick();
                    }
                });
            }
        }

        if (chunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING)) {
            for (BlockEntity value : chunk.getBlockEntities().values()) {
                if (value instanceof TrackedDataContainer access) {
                    Collection<TrackedDataKey<BlockEntityTrackedData>> keys = access.dataAnchor$getTrackedDataKeys();
                    for (TrackedDataKey<BlockEntityTrackedData> key : keys) {
                        access.dataAnchor$getTrackedData(key).ifPresent(data -> {
                            if (data instanceof TickableTrackedData tickableData) {
                                tickableData.tick();
                            }
                        });
                    }
                }
            }
        }
    }


    @Inject(method = "addRespawnedPlayer", at = @At("RETURN"))
    private void dataAnchor$addRespawnTeleport(ServerPlayer player, CallbackInfo ci) {
        if (player instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<PlayerTrackedData>> keys = access.dataAnchor$getTrackedDataKeys();
            for (TrackedDataKey<PlayerTrackedData> key : keys) {
                access.dataAnchor$getTrackedData(key).ifPresent(trackedData -> {
                    if (trackedData instanceof PlayerTrackedData data) {
                        data.addRespawnedPlayer();
                    }
                });
            }
        }
    }


    @Inject(method = "addDuringTeleport", at = @At("RETURN"))
    private void dataAnchor$addDuringPortalTeleport(Entity entity, CallbackInfo ci) {
        if (entity instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<PlayerTrackedData>> keys = access.dataAnchor$getTrackedDataKeys();
            for (TrackedDataKey<PlayerTrackedData> key : keys) {
                access.dataAnchor$getTrackedData(key).ifPresent(trackedData -> {
                    if (trackedData instanceof PlayerTrackedData data) {
                        data.addDuringPortalTeleport();
                    }
                });
            }
        }
    }

    @Inject(method = "addPlayer", at = @At("RETURN"))
    private void dataAnchor$onPlayerAddToWorld(ServerPlayer player, CallbackInfo ci) {
        if (player instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<PlayerTrackedData>> keys = access.dataAnchor$getTrackedDataKeys();
            for (TrackedDataKey<PlayerTrackedData> key : keys) {
                access.dataAnchor$getTrackedData(key).ifPresent(data -> {
                    if (data instanceof PlayerTrackedData playerTrackedData) {
                        playerTrackedData.playerAddedToWorld();
                        if (data instanceof SyncedPlayerTrackedData syncedData) {
                            syncedData.syncToPlayer(player);
                        }
                    }
                });
            }
        }
        if (this instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<LevelTrackedData>> keys = access.dataAnchor$getTrackedDataKeys();
            for (TrackedDataKey<LevelTrackedData> key : keys) {
                access.dataAnchor$getTrackedData(key).ifPresent(data -> {

                    if (data instanceof SyncedLevelTrackedData syncedData) {
                        syncedData.syncToPlayer(player);
                    }
                });
            }
        }
    }
}
