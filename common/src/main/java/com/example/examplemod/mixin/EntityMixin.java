package com.example.examplemod.mixin;

import com.example.examplemod.data.TickableTrackedData;
import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.registry.TrackedDataRegistries;
import com.example.examplemod.data.type.entity.EntityTrackedData;
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

    @Shadow public abstract Level level();

    @Unique
    private TrackedDataContainer<Entity, EntityTrackedData> exampleMod$container;

    @Unique
    private final Collection<TickableTrackedData> exampleMod$tickableData = new ArrayList<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(EntityType entityType, Level level, CallbackInfo ci) {
        this.create();
    }


    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void saveWithoutId(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag trackedData = new CompoundTag();
        Collection<TrackedDataKey<EntityTrackedData>> keys = this.exampleMod$container.getKeys();
        for (TrackedDataKey<EntityTrackedData> key : keys) {
            this.exampleMod$container.get(key).ifPresent(data -> {
                CompoundTag saveTag = data.save();
                if (saveTag != null) {
                    trackedData.put(key.getId().toString(), saveTag);
                }
            });
        }
        compound.put("TrackedData", trackedData);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void load(CompoundTag loadTag, CallbackInfo ci) {
        if (loadTag != null) {
            if (loadTag.contains("TrackedData")) {
                CompoundTag trackedData = loadTag.getCompound("TrackedData");
                Collection<TrackedDataKey<EntityTrackedData>> keys = this.exampleMod$container.getKeys();
                for (TrackedDataKey<EntityTrackedData> key : keys) {
                    String tagKey = key.getId().toString();
                    if (trackedData.contains(tagKey)) {
                        this.exampleMod$container.get(key).ifPresent(entityTrackedData -> entityTrackedData.load(trackedData.getCompound(tagKey)));
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        for (TickableTrackedData tickable : this.exampleMod$tickableData) {
            tickable.tick();
        }
    }

    @Override
    public <E extends EntityTrackedData> Optional<E> get(TrackedDataKey<E> key) {
        return this.exampleMod$container.get(key);
    }

    @Override
    public void create() {
        this.exampleMod$container = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.ENTITY, (Entity) (Object) this, level().isClientSide());
        this.exampleMod$container.create();
        for (TrackedDataKey<EntityTrackedData> key : this.exampleMod$container.getKeys()) {
            this.exampleMod$container.get(key).ifPresent(entityTrackedData -> {
                if (entityTrackedData instanceof TickableTrackedData tickable) {
                    this.exampleMod$tickableData.add(tickable);
                }
            });

        }
    }

    @Override
    public Collection<TrackedDataKey<EntityTrackedData>> getKeys() {
        return this.exampleMod$container.getKeys();
    }
}
