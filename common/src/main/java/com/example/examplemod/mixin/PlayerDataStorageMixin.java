package com.example.examplemod.mixin;

import com.example.examplemod.data.TrackedDataContainer;
import com.example.examplemod.data.TrackedDataKey;
import com.example.examplemod.data.player.PlayerTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;

@Mixin(PlayerDataStorage.class)
public class PlayerDataStorageMixin {

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Ljava/io/File;createTempFile(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;)Ljava/io/File;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onPlayerSave(Player player, CallbackInfo ci, CompoundTag compoundTag) {
        CompoundTag trackedData = new CompoundTag();
        if (player instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<PlayerTrackedData>> keys = access.getKeys();

            for (TrackedDataKey<PlayerTrackedData> key : keys) {
                PlayerTrackedData trackedData1 = (PlayerTrackedData) access.get(key);
                trackedData.put(key.getId().toString(), trackedData1.save());
            }
        }

        compoundTag.put("TrackedData", trackedData);

    }
}
