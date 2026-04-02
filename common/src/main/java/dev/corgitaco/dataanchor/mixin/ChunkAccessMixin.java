/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkBlockStateInterceptor;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Optional;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin implements TrackedDataContainer<ChunkAccess, ChunkTrackedData>, ChunkBlockStateInterceptor.Internal {

    @Unique
    TrackedDataContainer<ChunkAccess, ChunkTrackedData> dataAnchor$trackedDataContainer;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void dataAnchor$onInit(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, PalettedContainerFactory containerFactory, long inhabitedTime, LevelChunkSection[] sections, BlendingData blendingData, CallbackInfo ci) {
        if (levelHeightAccessor instanceof ServerLevelAccessor) {
            this.dataAnchor$trackedDataContainer = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.CHUNK, (ChunkAccess) (Object) this, false);
        } else {
            this.dataAnchor$trackedDataContainer = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.CHUNK, (ChunkAccess) (Object) this, true);
        }
        this.dataAnchor$createTrackedData();
    }

    @Override
    public <E extends ChunkTrackedData> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key) {
        return dataAnchor$trackedDataContainer.dataAnchor$getTrackedData(key);
    }

    @Override
    public void dataAnchor$createTrackedData() {
        dataAnchor$trackedDataContainer.dataAnchor$createTrackedData();
    }

    @Override
    public Collection<TrackedDataKey<ChunkTrackedData>> dataAnchor$getTrackedDataKeys() {
        return dataAnchor$trackedDataContainer.dataAnchor$getTrackedDataKeys();
    }

    @Override
    public @Nullable BlockState dataAnchor$getInterceptorState(BlockPos pos, BlockState original, @Nullable BlockState lastState, int flags) {
        BlockState replacement = lastState;
        Collection<TrackedDataKey<ChunkTrackedData>> collection = dataAnchor$getTrackedDataKeys();
        for (TrackedDataKey<ChunkTrackedData> dataAnchor$getTrackedDataKey : collection) {
            Optional<ChunkTrackedData> trackedData = dataAnchor$getTrackedData(dataAnchor$getTrackedDataKey);
            if (trackedData.isPresent()) {
                ChunkTrackedData chunkTrackedData = trackedData.get();
                if (chunkTrackedData instanceof ChunkBlockStateInterceptor chunkBlockStateInterceptor) {
                    replacement = chunkBlockStateInterceptor.getNewState(pos, original, replacement, flags);
                }
            }
        }


        return replacement;
    }
}
