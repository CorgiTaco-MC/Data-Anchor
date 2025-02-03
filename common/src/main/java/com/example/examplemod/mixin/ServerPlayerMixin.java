package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedDataAccess;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.PlayerTrackedData;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {


    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void restoreFrom(ServerPlayer oldPlayer, boolean keepEverything, CallbackInfo ci) {
        if (this instanceof TrackedDataAccess<?> access) {
            for (TrackedDataKey key : access.getKeys()) {
                PlayerTrackedData playerTrackedData = (PlayerTrackedData) access.get(key);
                playerTrackedData.onCopy(oldPlayer);

            }
        }
    }
}
