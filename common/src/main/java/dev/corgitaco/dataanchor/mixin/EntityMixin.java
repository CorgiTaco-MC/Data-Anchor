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
    private void init(EntityType entityType, Level level, CallbackInfo ci) {
        this.create();
    }


    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void saveWithoutId(CompoundTag compound, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag trackedData = new CompoundTag();
        Collection<TrackedDataKey<EntityTrackedData>> keys = this.dataAnchor$container.getKeys();
        for (TrackedDataKey<EntityTrackedData> key : keys) {
            this.dataAnchor$container.get(key).ifPresent(data -> {
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
                Collection<TrackedDataKey<EntityTrackedData>> keys = this.dataAnchor$container.getKeys();
                for (TrackedDataKey<EntityTrackedData> key : keys) {
                    String tagKey = key.getId().toString();
                    if (trackedData.contains(tagKey)) {
                        this.dataAnchor$container.get(key).ifPresent(entityTrackedData -> entityTrackedData.load(trackedData.getCompound(tagKey)));
                    }
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        for (TickableTrackedData tickable : this.dataAnchor$tickableData) {
            tickable.tick();
        }
    }

    @Override
    public <E extends EntityTrackedData> Optional<E> get(TrackedDataKey<E> key) {
        return this.dataAnchor$container.get(key);
    }

    @Override
    public void create() {
        this.dataAnchor$container = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.ENTITY, (Entity) (Object) this, this.level.isClientSide());
        this.dataAnchor$container.create();
        for (TrackedDataKey<EntityTrackedData> key : this.dataAnchor$container.getKeys()) {
            this.dataAnchor$container.get(key).ifPresent(entityTrackedData -> {
                if (entityTrackedData instanceof TickableTrackedData tickable) {
                    this.dataAnchor$tickableData.add(tickable);
                }
            });

        }
    }

    @Override
    public Collection<TrackedDataKey<EntityTrackedData>> getKeys() {
        return this.dataAnchor$container.getKeys();
    }
}
