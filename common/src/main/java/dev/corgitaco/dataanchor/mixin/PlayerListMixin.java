/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.entity.PlayerTrackedData;
import dev.corgitaco.dataanchor.data.type.entity.ServerPlayerTrackedData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(PlayerList.class)
public class PlayerListMixin {


    @Inject(method = "respawn", at = @At("RETURN"))
    private void dataAnchor$restoreFrom(ServerPlayer player, boolean keepInventory, Entity.RemovalReason reason, CallbackInfoReturnable<ServerPlayer> cir) {
        if (cir.getReturnValue() instanceof TrackedDataContainer access) {
            Collection<TrackedDataKey<PlayerTrackedData>> keys = access.dataAnchor$getTrackedDataKeys();
            keys.forEach(key -> {
                access.dataAnchor$getTrackedData(key).ifPresent(data -> {
                    if (data instanceof PlayerTrackedData playerTrackedData) {
                        playerTrackedData.respawn(player, keepInventory);
                    }
                });
            });
        }
    }
}
