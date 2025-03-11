package dev.corgitaco.dataanchor

import dev.corgitaco.dataanchor.data.registry.TrackedDataKey
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistry.TrackedDataFactory
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData
import dev.corgitaco.dataanchor.data.type.entity.EntityTrackedData
import dev.corgitaco.dataanchor.data.type.level.LevelTrackedData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.chunk.ChunkAccess
import kotlin.reflect.KClass

val <T : Entity> T.container get() = TrackedDataRegistries.ENTITY.getContainer(this)

val <T : BlockEntity> T.container get() = TrackedDataRegistries.BLOCK_ENTITY.getContainer(this)

val <T : Level> T.container get() = TrackedDataRegistries.LEVEL.getContainer(this)

val <T : ChunkAccess> T.container get() = TrackedDataRegistries.CHUNK.getContainer(this)

operator fun <T : Entity, E : EntityTrackedData> T.get(key: TrackedDataKey<E>) = container?.get(key)

operator fun <T : BlockEntity, E : BlockEntityTrackedData> T.get(key: TrackedDataKey<E>) = container?.get(key)

operator fun <T : Level, E : LevelTrackedData> T.get(key: TrackedDataKey<E>) = container?.get(key)

operator fun <T : ChunkAccess, E : ChunkTrackedData> T.get(key: TrackedDataKey<E>) = container?.get(key)

fun <E : EntityTrackedData> KClass<E>.entityDataOf(name: ResourceLocation, factory: TrackedDataFactory<Entity, E>): TrackedDataKey<E> = TrackedDataRegistries.ENTITY.register(name, java, factory)

fun <E : BlockEntityTrackedData> KClass<E>.blockEntityDataOf(name: ResourceLocation, factory: TrackedDataFactory<BlockEntity, E>): TrackedDataKey<E> = TrackedDataRegistries.BLOCK_ENTITY.register(name, java, factory)

fun <E : LevelTrackedData> KClass<E>.levelDataOf(name: ResourceLocation, factory: TrackedDataFactory<Level, E>): TrackedDataKey<E> = TrackedDataRegistries.LEVEL.register(name, java, factory)

fun <E : ChunkTrackedData> KClass<E>.chunkDataOf(name: ResourceLocation, factory: TrackedDataFactory<ChunkAccess, E>): TrackedDataKey<E> = TrackedDataRegistries.CHUNK.register(name, java, factory)