package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedDataAccess;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.PlayerTrackedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "addRespawnedPlayer", at = @At("RETURN"))
    private void addRespawnTeleport(ServerPlayer player, CallbackInfo ci) {
        if (player instanceof TrackedDataAccess<?> access) {
            for (TrackedDataKey key : access.getKeys()) {
                PlayerTrackedData data = (PlayerTrackedData) access.get(key);
                data.addRespawnedPlayer();
            }
        }
    }



    @Inject(method = "addDuringCommandTeleport", at = @At("RETURN"))
    private void addDuringCommandTeleport(ServerPlayer player, CallbackInfo ci) {
        if (player instanceof TrackedDataAccess<?> access) {
            for (TrackedDataKey key : access.getKeys()) {
                PlayerTrackedData data = (PlayerTrackedData) access.get(key);
                data.addDuringCommandTeleport();
            }
        }
    }



    @Inject(method = "addDuringPortalTeleport", at = @At("RETURN"))
    private void addDuringPortalTeleport(ServerPlayer player, CallbackInfo ci) {
        if (player instanceof TrackedDataAccess<?> access) {
            for (TrackedDataKey key : access.getKeys()) {
                PlayerTrackedData data = (PlayerTrackedData) access.get(key);
                data.addDuringPortalTeleport();
            }
        }
    }

    @Inject(method = "addPlayer", at = @At("RETURN"))
    private void onPlayerAddToWorld(ServerPlayer player, CallbackInfo ci) {
        if (player instanceof TrackedDataAccess<?> access) {
            for (TrackedDataKey key : access.getKeys()) {
                PlayerTrackedData data = (PlayerTrackedData) access.get(key);
                data.playerAddedToWorld();
            }
        }
    }

}
