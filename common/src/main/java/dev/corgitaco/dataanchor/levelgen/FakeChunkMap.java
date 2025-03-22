package dev.corgitaco.dataanchor.levelgen;

import com.mojang.datafixers.DataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class FakeChunkMap extends ChunkMap {
    public FakeChunkMap(ServerLevel level, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer fixerUpper, StructureTemplateManager structureManager, Executor dispatcher, BlockableEventLoop<Runnable> mainThreadExecutor, LightChunkGetter lightChunk, ChunkGenerator generator, ChunkProgressListener progressListener, ChunkStatusUpdateListener chunkStatusListener, Supplier<DimensionDataStorage> overworldDataStorage, int viewDistance, boolean sync) {
        super(level, levelStorageAccess, fixerUpper, structureManager, dispatcher, mainThreadExecutor, lightChunk, generator, progressListener, chunkStatusListener, overworldDataStorage, viewDistance, sync);
    }

    @Override
    public boolean save(ChunkAccess chunk) {
        return true;
    }

    @Override
    public CompletableFuture<Optional<CompoundTag>> readChunk(ChunkPos pos) {
        return CompletableFuture.completedFuture(Optional.empty());
    }
}
