package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.PlayerTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(PlayerList.class)
public class PlayerListMixin {


    @Inject(method = "load", at = @At("RETURN"))
    private void read(ServerPlayer player, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag loadTag = cir.getReturnValue();
        if (loadTag != null) {
            if (player instanceof TrackedDataContainer trackedDataContainer) {
                if (loadTag.contains("TrackedData")) {
                    CompoundTag trackedData = loadTag.getCompound("TrackedData");
                    Collection<TrackedDataKey<PlayerTrackedData>> keys = trackedDataContainer.getKeys();
                    for (TrackedDataKey<PlayerTrackedData> key : keys) {
                        String tagKey = key.getId().toString();
                        if (trackedData.contains(tagKey)) {
                            trackedDataContainer.get(key).load(trackedData.getCompound(tagKey));
                        }
                    }
                }
            }
        }

    }
}
