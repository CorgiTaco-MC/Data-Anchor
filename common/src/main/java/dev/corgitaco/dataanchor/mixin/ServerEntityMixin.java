/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.entity.EntityTrackedData;
import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @Shadow
    @Final
    private Entity entity;

    @WrapOperation(method = "addPairing", at = @At(value = "NEW", target = "(Ljava/lang/Iterable;)Lnet/minecraft/network/protocol/game/ClientboundBundlePacket;"))
    private ClientboundBundlePacket dataAnchor$addPairing(Iterable iterable, Operation<ClientboundBundlePacket> original, @Local(argsOnly = true) ServerPlayer serverPlayer) {
        if (this.entity instanceof TrackedDataContainer trackedDataContainer) {
            Collection<TrackedDataKey<EntityTrackedData>> keys = trackedDataContainer.dataAnchor$getTrackedDataKeys();
            keys.forEach(key -> {
                trackedDataContainer.dataAnchor$getTrackedData(key).ifPresent(trackedData -> {
                    if (trackedData instanceof SyncedTrackedData syncedTrackedData) {
                        syncedTrackedData.syncToPlayer(serverPlayer);
                        if (iterable instanceof Collection collection) {
                            Packet packet = syncedTrackedData.syncPacket();
                            collection.add(new ClientboundCustomPayloadPacket(packet));
                        }
                    }
                });
            });
        }
        return original.call(iterable);
    }
}