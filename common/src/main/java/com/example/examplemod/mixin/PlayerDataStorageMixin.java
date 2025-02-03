package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedData;
import com.example.examplemod.data.TrackedDataAccess;
import com.example.examplemod.data.TrackedDataKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerDataStorage.class)
public class PlayerDataStorageMixin {

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Ljava/io/File;createTempFile(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;)Ljava/io/File;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onPlayerSave(Player player, CallbackInfo ci, CompoundTag compoundTag) {
        CompoundTag trackedData = new CompoundTag();
        if (player instanceof TrackedDataAccess<?> access) {
            for (TrackedDataKey key : access.getKeys()) {
                TrackedData trackedData1 = access.get(key);
                trackedData.put(key.getId().toString(), trackedData1.save());
            }
        }

        compoundTag.put("TrackedData", trackedData);

    }
}
