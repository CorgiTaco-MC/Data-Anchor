package dev.corgitaco.dataanchor.data.registry;

import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData;
import dev.corgitaco.dataanchor.data.type.entity.EntityTrackedData;
import dev.corgitaco.dataanchor.data.type.level.LevelTrackedData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;

public class TrackedDataRegistries {
    public static final TrackedDataRegistry<Entity, EntityTrackedData> ENTITY = TrackedDataRegistry.of(DataAnchor.id("entity"));
    public static final TrackedDataRegistry<BlockEntity, BlockEntityTrackedData> BLOCK_ENTITY = TrackedDataRegistry.of(DataAnchor.id("block_entity"));
    public static final TrackedDataRegistry<ChunkAccess, ChunkTrackedData> CHUNK = TrackedDataRegistry.of(DataAnchor.id("chunk"));
    public static final TrackedDataRegistry<Level, LevelTrackedData> LEVEL = TrackedDataRegistry.of(DataAnchor.id("level"));
}