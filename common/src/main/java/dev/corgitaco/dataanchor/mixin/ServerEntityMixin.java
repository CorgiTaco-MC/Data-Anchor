/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.TrackedDataContainer;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.entity.EntityTrackedData;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
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
import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {

    @Shadow
    @Final
    private Entity entity;

    @Inject(method = "addPairing", at = @At(value = "RETURN"))
    private void dataAnchor$addPairing(ServerPlayer player, CallbackInfo ci) {
        if (this.entity instanceof TrackedDataContainer trackedDataContainer) {
            Collection<TrackedDataKey<EntityTrackedData>> keys = trackedDataContainer.dataAnchor$getTrackedDataKeys();
            keys.forEach(key -> {
                trackedDataContainer.dataAnchor$getTrackedData(key).ifPresent(trackedData -> {
                    if (trackedData instanceof SyncedTrackedData syncedTrackedData) {
                        syncedTrackedData.syncToPlayer(player);
                    }
                });
            });
        }
    }

    @Inject(method = "sendPairingData", at = @At("RETURN"))
    private void dataAnchor$sendPairingData(ServerPlayer player, Consumer<net.minecraft.network.protocol.Packet<ClientGamePacketListener>> consumer, CallbackInfo ci) {
        if (this.entity instanceof TrackedDataContainer trackedDataContainer) {
            Collection<TrackedDataKey<EntityTrackedData>> keys = trackedDataContainer.dataAnchor$getTrackedDataKeys();
            keys.forEach(key -> {
                trackedDataContainer.dataAnchor$getTrackedData(key).ifPresent(trackedData -> {
                    if (trackedData instanceof SyncedTrackedData syncedTrackedData) {
                        Packet packet = syncedTrackedData.syncPacket();
                        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
                        packet.write(friendlyByteBuf);
                        consumer.accept(new ClientboundCustomPayloadPacket(PacketBroadcaster.S2C.channelName(packet.getClass()), friendlyByteBuf));
                        syncedTrackedData.syncToPlayer(player);
                    }
                });
            });
        }
    }
}