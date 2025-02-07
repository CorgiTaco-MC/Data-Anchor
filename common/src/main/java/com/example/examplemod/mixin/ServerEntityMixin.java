package com.example.examplemod.mixin;

import com.example.examplemod.data.SyncedTrackedData;
import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.type.entity.PlayerTrackedData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "addPairing", at = @At("RETURN"))
    private void addPairing(ServerPlayer player, CallbackInfo ci) {
        if (this.entity instanceof TrackedDataContainer trackedDataContainer) {
            Collection<TrackedDataKey<PlayerTrackedData>> keys = trackedDataContainer.getKeys();
            keys.forEach(key -> {
                trackedDataContainer.get(key).ifPresent(trackedData -> {
                    if (trackedData instanceof SyncedTrackedData syncedTrackedData) {
                        syncedTrackedData.syncToPlayer(player);
                    }
                });
            });
        }
    }
}
