package com.example.examplemod.mixin;

import com.example.examplemod.data.*;
import com.example.examplemod.data.level.LevelTrackedData;
import com.example.examplemod.data.level.TrackedLevelSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(Level.class)
public class LevelMixin implements TrackedDataContainer<Level, LevelTrackedData>, DirtyMarker {
    @Unique
    private TrackedDataContainer<Level, LevelTrackedData> exampleMod$trackedDataContainer = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.LEVEL, (Level) (Object) this);

    @Unique
    private final List<TickableTrackedData> exampleMod$tickableLevelData = new ArrayList<>();

    @Override
    public <E extends LevelTrackedData> E get(TrackedDataKey<E> key) {
        return this.exampleMod$trackedDataContainer.get(key);
    }

    @Override
    public void create() {
        this.exampleMod$trackedDataContainer = (Object) this instanceof ServerLevel serverLevel ? TrackedLevelSavedData.get(serverLevel) : TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.LEVEL, (Level) (Object) this);

        this.exampleMod$trackedDataContainer.create();

        for (TrackedDataKey<LevelTrackedData> key : this.exampleMod$trackedDataContainer.getKeys()) {
            LevelTrackedData levelTrackedData = this.exampleMod$trackedDataContainer.get(key);
            if (levelTrackedData instanceof TickableTrackedData tickableData) {
                this.exampleMod$tickableLevelData.add(tickableData);
            }
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
            LevelTrackedData levelTrackedData = exampleMod$trackedDataContainer.get(key);
            if (levelTrackedData instanceof DirtyMarker dirtyMarker) {
                dirtyMarker.clearDirty();
            }
        });
    }
}