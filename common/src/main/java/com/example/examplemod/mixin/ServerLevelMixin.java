package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.level.LevelTrackedData;
import com.example.examplemod.data.level.SyncedLevelTrackedData;
import com.example.examplemod.data.player.PlayerTrackedData;
import com.example.examplemod.data.player.SyncedPlayerTrackedData;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {

    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void createTrackedData(MinecraftServer server, Executor dispatcher, LevelStorageSource.LevelStorageAccess levelStorageAccess, ServerLevelData serverLevelData, ResourceKey dimension, LevelStem levelStem, ChunkProgressListener progressListener, boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime, RandomSequences randomSequences, CallbackInfo ci) {
        if (this instanceof TrackedDataContainer access) {
            access.create();
        }
    }

    @Inject(method = "addRespawnedPlayer", at = @At("RETURN"))
    private void addRespawnTeleport(ServerPlayer player, CallbackInfo ci) {
        if (player instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<PlayerTrackedData>> keys = access.getKeys();
            for (TrackedDataKey<PlayerTrackedData> key : keys) {
                PlayerTrackedData data = (PlayerTrackedData) access.get(key);
                data.addRespawnedPlayer();
            }
        }
    }


    @Inject(method = "addDuringCommandTeleport", at = @At("RETURN"))
    private void addDuringCommandTeleport(ServerPlayer player, CallbackInfo ci) {
        if (player instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<PlayerTrackedData>> keys = access.getKeys();
            for (TrackedDataKey<PlayerTrackedData> key : keys) {
                PlayerTrackedData data = (PlayerTrackedData) access.get(key);
                data.addDuringCommandTeleport();
            }
        }
    }


    @Inject(method = "addDuringPortalTeleport", at = @At("RETURN"))
    private void addDuringPortalTeleport(ServerPlayer player, CallbackInfo ci) {
        if (player instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<PlayerTrackedData>> keys = access.getKeys();
            for (TrackedDataKey<PlayerTrackedData> key : keys) {
                PlayerTrackedData data = (PlayerTrackedData) access.get(key);
                data.addDuringPortalTeleport();
            }
        }
    }

    @Inject(method = "addPlayer", at = @At("RETURN"))
    private void onPlayerAddToWorld(ServerPlayer player, CallbackInfo ci) {
        if (player instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<PlayerTrackedData>> keys = access.getKeys();
            for (TrackedDataKey<PlayerTrackedData> key : keys) {
                PlayerTrackedData data = (PlayerTrackedData) access.get(key);
                data.playerAddedToWorld();
                if (data instanceof SyncedPlayerTrackedData syncedData) {
                    syncedData.syncToPlayer(player);
                }
            }
        }
        if(this instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<LevelTrackedData>> keys = access.getKeys();
            for (TrackedDataKey<LevelTrackedData> key : keys) {
                LevelTrackedData data = (LevelTrackedData) access.get(key);
                if(data instanceof SyncedLevelTrackedData syncedData) {
                    syncedData.syncToPlayer(player);
                }
            }
        }
    }
}
