package com.example.examplemod.mixin.client;

import com.example.examplemod.data.TrackedData;
import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.PlayerTrackedData;
import net.minecraft.client.server.IntegratedPlayerList;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(IntegratedPlayerList.class)
public abstract class IntegratedPlayerListMixin {

    @Shadow public abstract IntegratedServer getServer();

    @Shadow private CompoundTag playerData;

    @Inject(method = "save", at = @At(value = "RETURN"))
    private void save(ServerPlayer player, CallbackInfo ci) {
        if (getServer().isSingleplayerOwner(player.getGameProfile())) {
            CompoundTag trackedData = new CompoundTag();
            if (player instanceof TrackedDataContainer access) {
                Collection<TrackedDataKey<PlayerTrackedData>> keys = access.getKeys();
                for (TrackedDataKey key : keys) {
                    TrackedData trackedData1 = access.get(key);
                    trackedData.put(key.getId().toString(), trackedData1.save());
                }
            }
            this.playerData.put("TrackedData", trackedData);
        }
    }
}
