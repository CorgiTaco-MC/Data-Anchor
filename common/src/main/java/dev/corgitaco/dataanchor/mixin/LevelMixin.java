/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.DirtyMarker;
import dev.corgitaco.dataanchor.data.InternalDirtyMarker;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
import dev.corgitaco.dataanchor.data.type.blockentity.PendingBlockEntityTick;
import dev.corgitaco.dataanchor.data.type.level.LevelTrackedData;
import dev.corgitaco.dataanchor.data.type.level.TrackedLevelSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(Level.class)
public abstract class LevelMixin implements TrackedDataContainer<Level, LevelTrackedData>, InternalDirtyMarker, LevelAccessor {
    @Shadow
    public abstract boolean isClientSide();

    @Shadow
    @Final
    public boolean isClientSide;
    @Unique
    private TrackedDataContainer<Level, LevelTrackedData> dataAnchor$trackedDataContainer = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.LEVEL, (Level) (Object) this, isClientSide(), false);

    @Unique
    private final List<TickableTrackedData> dataAnchor$tickableLevelData = new ArrayList<>();

    @Unique
    private boolean dataAnchor$lazyLoadedTrackedData = false;

    @Override
    public <E extends LevelTrackedData> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key) {
        if (!dataAnchor$lazyLoadedTrackedData) {
            dataAnchor$createTrackedData();
            dataAnchor$lazyLoadedTrackedData = true;
        }

        return this.dataAnchor$trackedDataContainer.dataAnchor$getTrackedData(key);
    }

    @Override
    public void dataAnchor$createTrackedData() {
        if ((Object) this instanceof ServerLevel serverLevel) {
            this.dataAnchor$trackedDataContainer = TrackedLevelSavedData.get(serverLevel);
        } else {
            this.dataAnchor$trackedDataContainer.dataAnchor$createTrackedData();
        }

        for (TrackedDataKey<LevelTrackedData> key : this.dataAnchor$trackedDataContainer.dataAnchor$getTrackedDataKeys()) {
            this.dataAnchor$trackedDataContainer.dataAnchor$getTrackedData(key).ifPresent(levelTrackedData -> {
                if (levelTrackedData instanceof TickableTrackedData tickableData) {
                    this.dataAnchor$tickableLevelData.add(tickableData);
                }
            });
        }
    }

    @Override
    public Collection<TrackedDataKey<LevelTrackedData>> dataAnchor$getTrackedDataKeys() {
        if (!dataAnchor$lazyLoadedTrackedData) {
            dataAnchor$createTrackedData();
            dataAnchor$lazyLoadedTrackedData = true;
        }
        return this.dataAnchor$trackedDataContainer.dataAnchor$getTrackedDataKeys();
    }

    @Inject(method = "tickBlockEntities", at = @At("RETURN"))
    private void onTickBlockEntities(CallbackInfo ci) {
        for (TickableTrackedData tickableLevelDatum : this.dataAnchor$tickableLevelData) {
            tickableLevelDatum.tick();
        }
    }

    @Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void dataAnchor$onTickBlockEntitiesEnd(CallbackInfo ci, ProfilerFiller profilerFiller, Iterator iterator, TickingBlockEntity tickingBlockEntity) {
        if (this.getBlockEntity(tickingBlockEntity.getPos()) instanceof TrackedDataContainer container) {
            Collection<TrackedDataKey<BlockEntityTrackedData>> keys = container.dataAnchor$getTrackedDataKeys();
            for (TrackedDataKey<BlockEntityTrackedData> key : keys) {
                container.dataAnchor$getTrackedData(key).ifPresent(data -> {
                    if (data instanceof PendingBlockEntityTick tickableData) {
                        tickableData.blockEntityTick();
                    }
                });
            }
        }
    }

    @Override
    public void dataAnchor$markDirty() {
        if (dataAnchor$trackedDataContainer instanceof TrackedLevelSavedData dirtyMarker) {
            dirtyMarker.setDirty();
        }
    }

    @Override
    public void dataAnchor$clearDirty() {
        dataAnchor$trackedDataContainer.dataAnchor$getTrackedDataKeys().forEach(key -> {
            dataAnchor$trackedDataContainer.dataAnchor$getTrackedData(key).ifPresent(levelTrackedData -> {
                if (levelTrackedData instanceof DirtyMarker dirtyMarker) {
                    dirtyMarker.clearDirty();
                }
            });
        });
    }
}