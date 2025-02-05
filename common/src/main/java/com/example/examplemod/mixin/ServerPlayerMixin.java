package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.PlayerTrackedData;
import com.example.examplemod.data.player.ServerPlayerTrackedData;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {


    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void restoreFrom(ServerPlayer oldPlayer, boolean keepEverything, CallbackInfo ci) {
        if (this instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<ServerPlayerTrackedData>> keys = access.getKeys();
            keys.forEach(key -> {
                PlayerTrackedData playerTrackedData = (PlayerTrackedData) access.get(key);
                playerTrackedData.copy(oldPlayer, keepEverything);
            });
        }
    }
}
