package com.example.examplemod.data.registry;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.data.TrackedData;
import com.example.examplemod.data.type.chunk.ChunkTrackedData;
import com.example.examplemod.data.type.entity.EntityTrackedData;
import com.example.examplemod.data.type.level.LevelTrackedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;

public class TrackedDataRegistries {
    public static final TrackedDataRegistry<Entity, EntityTrackedData> ENTITY = TrackedDataRegistry.of(ExampleMod.id("entity"));
    public static final TrackedDataRegistry<BlockEntity, TrackedData<BlockEntity>> BLOCK_ENTITY = TrackedDataRegistry.of(ExampleMod.id("block_entity"));
    public static final TrackedDataRegistry<ChunkAccess, ChunkTrackedData> CHUNK = TrackedDataRegistry.of(ExampleMod.id("chunk"));
    public static final TrackedDataRegistry<Level, LevelTrackedData> LEVEL = TrackedDataRegistry.of(ExampleMod.id("level"));
    public static final TrackedDataRegistry<MinecraftServer, TrackedData<MinecraftServer>> SERVER = TrackedDataRegistry.of(ExampleMod.id("server"));
}