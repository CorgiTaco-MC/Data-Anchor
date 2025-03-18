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
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Mixin(BlockEntity.class)
public class BlockEntityMixin implements TrackedDataContainer<BlockEntity, BlockEntityTrackedData> {

    @Shadow
    @Nullable
    protected Level level;
    @Unique
    @Nullable
    private TrackedDataContainer<BlockEntity, BlockEntityTrackedData> dataAnchor$container;


    @Inject(method = "setLevel", at = @At("RETURN"))
    private void dataAnchor$setLevel(Level level, CallbackInfo ci) {
        dataAnchor$createTrackedData();
    }

    @Override
    public <E extends BlockEntityTrackedData> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key) {
        if (dataAnchor$container == null) {
            return Optional.empty();
        }
        return this.dataAnchor$container.dataAnchor$getTrackedData(key);
    }

    @Override
    public void dataAnchor$createTrackedData() {
        if (dataAnchor$container == null) {
            dataAnchor$container = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.BLOCK_ENTITY, (BlockEntity) (Object) this, level != null/*assume loading from disk*/ && level.isClientSide());
            this.dataAnchor$container.dataAnchor$createTrackedData();
        }
    }

    @Override
    public Collection<TrackedDataKey<BlockEntityTrackedData>> dataAnchor$getTrackedDataKeys() {
        if (dataAnchor$container == null) {
            return Collections.emptyList();
        }
        return this.dataAnchor$container.dataAnchor$getTrackedDataKeys();
    }

    @Inject(method = "loadStatic", at = @At("RETURN"))
    private static void dataAnchor$loadStatic(BlockPos pos, BlockState state, CompoundTag tag, HolderLookup.Provider registries, CallbackInfoReturnable<BlockEntity> cir) {
        if (cir.getReturnValue() instanceof TrackedDataContainer container) {
            container.dataAnchor$createTrackedData();
            if (tag.contains("TrackedData")) {
                CompoundTag trackedData = tag.getCompound("TrackedData");
                Collection<TrackedDataKey<BlockEntityTrackedData>> keys = container.dataAnchor$getTrackedDataKeys();
                for (TrackedDataKey<BlockEntityTrackedData> key : keys) {
                    container.dataAnchor$getTrackedData(key).ifPresent(data -> {
                        if (data instanceof BlockEntityTrackedData blockEntityTrackedData) {
                            String idString = key.getId().toString();
                            if (trackedData.contains(idString)) {
                                blockEntityTrackedData.load(trackedData.getCompound(idString));
                            }
                        }
                    });
                }
            }
        }
    }

    @Inject(method = "saveWithFullMetadata", at = @At("RETURN"))
    private void dataAnchor$saveWithFullMetadata(CallbackInfoReturnable<CompoundTag> cir) {
        if (this.dataAnchor$container != null) {
            CompoundTag trackedData = new CompoundTag();
            for (TrackedDataKey<BlockEntityTrackedData> key : this.dataAnchor$container.dataAnchor$getTrackedDataKeys()) {
                this.dataAnchor$container.dataAnchor$getTrackedData(key).ifPresent(data -> {
                    CompoundTag save = data.save();
                    if (save != null) {
                        trackedData.put(key.getId().toString(), save);
                    }
                });
            }
            cir.getReturnValue().put("TrackedData", trackedData);
        }
    }
}
