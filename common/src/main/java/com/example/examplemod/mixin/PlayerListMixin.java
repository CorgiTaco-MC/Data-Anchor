package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedDataAccess;
import com.example.examplemod.data.TrackedDataKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {


    @Inject(method = "load", at = @At("RETURN"))
    private void read(ServerPlayer player, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag loadTag = cir.getReturnValue();
        if (loadTag != null) {
            if (player instanceof TrackedDataAccess<?> trackedDataAccess) {
                if (loadTag.contains("TrackedData")) {
                    CompoundTag trackedData = loadTag.getCompound("TrackedData");
                    for (TrackedDataKey key : trackedDataAccess.getKeys()) {
                        String tagKey = key.getId().toString();
                        if (trackedData.contains(tagKey)) {
                            trackedDataAccess.get(key).load(trackedData.getCompound(tagKey));
                        }
                    }
                }
            }
        }

    }
}
