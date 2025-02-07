package com.example.examplemod.mixin;

import com.example.examplemod.data.DirtyMarker;
import com.example.examplemod.data.TickableTrackedData;
import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.registry.TrackedDataRegistries;
import com.example.examplemod.data.type.level.LevelTrackedData;
import com.example.examplemod.data.type.level.TrackedLevelSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mixin(Level.class)
public abstract class LevelMixin implements TrackedDataContainer<Level, LevelTrackedData>, DirtyMarker {
    @Shadow public abstract boolean isClientSide();

    @Unique
    private TrackedDataContainer<Level, LevelTrackedData> exampleMod$trackedDataContainer = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.LEVEL, (Level) (Object) this, isClientSide());

    @Unique
    private final List<TickableTrackedData> exampleMod$tickableLevelData = new ArrayList<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (((Level) (Object) this instanceof ServerLevel)) {
            return;
        }
        this.create();
    }

    @Override
    public <E extends LevelTrackedData> Optional<E> get(TrackedDataKey<E> key) {
        return this.exampleMod$trackedDataContainer.get(key);
    }

    @Override
    public void create() {
        if ((Object) this instanceof ServerLevel serverLevel) {
            this.exampleMod$trackedDataContainer = TrackedLevelSavedData.get(serverLevel);
        }

        for (TrackedDataKey<LevelTrackedData> key : this.exampleMod$trackedDataContainer.getKeys()) {
            this.exampleMod$trackedDataContainer.get(key).ifPresent(levelTrackedData -> {
                if (levelTrackedData instanceof TickableTrackedData tickableData) {
                    this.exampleMod$tickableLevelData.add(tickableData);
                }
            });
        }
    }

    @Override
    public Collection<TrackedDataKey<LevelTrackedData>> getKeys() {
        return this.exampleMod$trackedDataContainer.getKeys();
    }

    @Inject(method = "tickBlockEntities", at = @At("RETURN"))
    private void onTickBlockEntities(CallbackInfo ci) {
        for (TickableTrackedData tickableLevelDatum : this.exampleMod$tickableLevelData) {
            tickableLevelDatum.tick();
        }
    }

    @Override
    public void markDirty() {
        if (exampleMod$trackedDataContainer instanceof TrackedLevelSavedData dirtyMarker) {
            dirtyMarker.setDirty();
        }
    }

    @Override
    public void clearDirty() {
        exampleMod$trackedDataContainer.getKeys().forEach(key -> {
             exampleMod$trackedDataContainer.get(key).ifPresent(levelTrackedData -> {
                if (levelTrackedData instanceof DirtyMarker dirtyMarker) {
                    dirtyMarker.clearDirty();
                }
             });
        });
    }
}