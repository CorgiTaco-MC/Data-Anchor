package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.DirtyMarker;
import dev.corgitaco.dataanchor.data.TickableTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
import dev.corgitaco.dataanchor.data.type.level.LevelTrackedData;
import dev.corgitaco.dataanchor.data.type.level.TrackedLevelSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(Level.class)
public abstract class LevelMixin implements TrackedDataContainer<Level, LevelTrackedData>, DirtyMarker {
    @Shadow
    public abstract boolean isClientSide();

    @Unique
    private TrackedDataContainer<Level, LevelTrackedData> dataAnchor$trackedDataContainer = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.LEVEL, (Level) (Object) this, isClientSide());

    @Unique
    private final List<TickableTrackedData> dataAnchor$tickableLevelData = new ArrayList<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (((Level) (Object) this instanceof ServerLevel)) {
            return;
        }
        this.create();
    }

    @Override
    public <E extends LevelTrackedData> Optional<E> get(TrackedDataKey<E> key) {
        return this.dataAnchor$trackedDataContainer.get(key);
    }

    @Override
    public void create() {
        if ((Object) this instanceof ServerLevel serverLevel) {
            this.dataAnchor$trackedDataContainer = TrackedLevelSavedData.get(serverLevel);
        }

        for (TrackedDataKey<LevelTrackedData> key : this.dataAnchor$trackedDataContainer.getKeys()) {
            this.dataAnchor$trackedDataContainer.get(key).ifPresent(levelTrackedData -> {
                if (levelTrackedData instanceof TickableTrackedData tickableData) {
                    this.dataAnchor$tickableLevelData.add(tickableData);
                }
            });
        }
    }

    @Override
    public Collection<TrackedDataKey<LevelTrackedData>> getKeys() {
        return this.dataAnchor$trackedDataContainer.getKeys();
    }

    @Inject(method = "tickBlockEntities", at = @At("RETURN"))
    private void onTickBlockEntities(CallbackInfo ci) {
        for (TickableTrackedData tickableLevelDatum : this.dataAnchor$tickableLevelData) {
            tickableLevelDatum.tick();
        }
    }

    @Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onTickBlockEntitiesEnd(CallbackInfo ci, ProfilerFiller profilerFiller, Iterator iterator, TickingBlockEntity tickingBlockEntity) {
        if (((Level) (Object) this).getBlockEntity(tickingBlockEntity.getPos()) instanceof TrackedDataContainer container) {
            Collection<TrackedDataKey<BlockEntityTrackedData>> keys = container.getKeys();
            for (TrackedDataKey<BlockEntityTrackedData> key : keys) {
                container.get(key).ifPresent(data -> {
                    if (data instanceof TickableTrackedData tickableData) {
                        tickableData.tick();
                    }
                });
            }
        }
    }

    @Override
    public void markDirty() {
        if (dataAnchor$trackedDataContainer instanceof TrackedLevelSavedData dirtyMarker) {
            dirtyMarker.setDirty();
        }
    }

    @Override
    public void clearDirty() {
        dataAnchor$trackedDataContainer.getKeys().forEach(key -> {
            dataAnchor$trackedDataContainer.get(key).ifPresent(levelTrackedData -> {
                if (levelTrackedData instanceof DirtyMarker dirtyMarker) {
                    dirtyMarker.clearDirty();
                }
            });
        });
    }
}