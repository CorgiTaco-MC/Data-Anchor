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
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.entity.EntityTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements TrackedDataContainer<Entity, EntityTrackedData> {

    @Shadow private Level level;
    @Unique
    private TrackedDataContainer<Entity, EntityTrackedData> dataAnchor$container;

    @Unique
    private final Collection<TickableTrackedData> dataAnchor$tickableData = new ArrayList<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void dataAnchor$init(EntityType entityType, Level level, CallbackInfo ci) {
        this.dataAnchor$createTrackedData();
    }


    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void dataAnchor$saveWithoutId(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag trackedData = new CompoundTag();
        Collection<TrackedDataKey<EntityTrackedData>> keys = this.dataAnchor$container.dataAnchor$getTrackedDataKeys();
        for (TrackedDataKey<EntityTrackedData> key : keys) {
            this.dataAnchor$container.dataAnchor$getTrackedData(key).ifPresent(data -> {
                CompoundTag saveTag = data.save();
                if (saveTag != null) {
                    trackedData.put(key.getId().toString(), saveTag);
                }
            });
        }
        compound.put("TrackedData", trackedData);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void dataAnchor$load(CompoundTag loadTag, CallbackInfo ci) {
        if (loadTag != null) {
            if (loadTag.contains("TrackedData")) {
                CompoundTag trackedData = loadTag.getCompound("TrackedData");
                Collection<TrackedDataKey<EntityTrackedData>> keys = this.dataAnchor$container.dataAnchor$getTrackedDataKeys();
                for (TrackedDataKey<EntityTrackedData> key : keys) {
                    String tagKey = key.getId().toString();
                    if (trackedData.contains(tagKey)) {
                        this.dataAnchor$container.dataAnchor$getTrackedData(key).ifPresent(entityTrackedData -> entityTrackedData.load(trackedData.getCompound(tagKey)));
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void dataAnchor$tick(CallbackInfo ci) {
        for (TickableTrackedData tickable : this.dataAnchor$tickableData) {
            tickable.tick();
        }
    }

    @Override
    public <E extends EntityTrackedData> Optional<E> dataAnchor$getTrackedData(TrackedDataKey<E> key) {
        return this.dataAnchor$container.dataAnchor$getTrackedData(key);
    }

    @Override
    public void dataAnchor$createTrackedData() {
        this.dataAnchor$container = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.ENTITY, (Entity) (Object) this, this.level.isClientSide());
        this.dataAnchor$container.dataAnchor$createTrackedData();
        for (TrackedDataKey<EntityTrackedData> key : this.dataAnchor$container.dataAnchor$getTrackedDataKeys()) {
            this.dataAnchor$container.dataAnchor$getTrackedData(key).ifPresent(entityTrackedData -> {
                if (entityTrackedData instanceof TickableTrackedData tickable) {
                    this.dataAnchor$tickableData.add(tickable);
                }
            });

        }
    }

    @Override
    public Collection<TrackedDataKey<EntityTrackedData>> dataAnchor$getTrackedDataKeys() {
        return this.dataAnchor$container.dataAnchor$getTrackedDataKeys();
    }
}
