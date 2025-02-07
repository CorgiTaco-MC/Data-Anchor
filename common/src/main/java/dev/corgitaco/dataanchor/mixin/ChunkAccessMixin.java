package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Optional;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin implements TrackedDataContainer<ChunkAccess, ChunkTrackedData> {

    @Unique
    TrackedDataContainer<ChunkAccess, ChunkTrackedData> dataAnchor$trackedDataContainer = TrackedDataContainer.makeBasicContainer(TrackedDataRegistries.CHUNK, (ChunkAccess) (Object) this, true);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry biomeRegistry, long inhabitedTime, LevelChunkSection[] sections, BlendingData blendingData, CallbackInfo ci) {
        this.create();
    }

    @Override
    public <E extends ChunkTrackedData> Optional<E> get(TrackedDataKey<E> key) {
        return dataAnchor$trackedDataContainer.get(key);
    }

    @Override
    public void create() {
        dataAnchor$trackedDataContainer.create();
    }

    @Override
    public Collection<TrackedDataKey<ChunkTrackedData>> getKeys() {
        return dataAnchor$trackedDataContainer.getKeys();
    }


}
