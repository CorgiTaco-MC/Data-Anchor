/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.entity.EntityTrackedData;
import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
import java.util.List;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "addPairing", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerEntity;sendPairingData(Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V"))
    private void dataAnchor$addPairing(ServerPlayer player, CallbackInfo ci, @Local List<net.minecraft.network.protocol.Packet<? super ClientGamePacketListener>> list) {
        if (this.entity instanceof TrackedDataContainer trackedDataContainer) {
            Collection<TrackedDataKey<EntityTrackedData>> keys = trackedDataContainer.dataAnchor$getTrackedDataKeys();
            keys.forEach(key -> {
                trackedDataContainer.dataAnchor$getTrackedData(key).ifPresent(trackedData -> {
                    if (trackedData instanceof SyncedTrackedData syncedTrackedData) {
                        Packet packet = syncedTrackedData.syncPacket();
                        list.add(new ClientboundCustomPayloadPacket(packet));
                        syncedTrackedData.syncToPlayer(player);
                    }
                });
            });
        }
    }
}