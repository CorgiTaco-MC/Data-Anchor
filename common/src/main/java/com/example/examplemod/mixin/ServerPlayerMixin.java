package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.registry.TrackedDataKey;
import com.example.examplemod.data.type.entity.PlayerTrackedData;
import com.example.examplemod.data.type.entity.ServerPlayerTrackedData;
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
                access.get(key).ifPresent(data -> {
                    if (data instanceof PlayerTrackedData playerTrackedData) {
                        playerTrackedData.copy(oldPlayer, keepEverything);
                    }
                });
            });
        }
    }
}
