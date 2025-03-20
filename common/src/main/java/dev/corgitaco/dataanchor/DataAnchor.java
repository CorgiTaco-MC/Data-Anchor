/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor;

import com.mojang.logging.LogUtils;
import dev.corgitaco.dataanchor.data.type.blockentity.network.SyncBlockEntityTrackedDataS2C;
import dev.corgitaco.dataanchor.data.type.chunk.network.SyncLevelChunkTrackedDataS2C;
import dev.corgitaco.dataanchor.data.type.entity.network.SyncEntityTrackedDataS2C;
import dev.corgitaco.dataanchor.data.type.level.network.SyncLevelTrackedDataS2C;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.S2CNetworkContainer;
import dev.corgitaco.dataanchor.storage._2D.QuadTreeNearestPoint;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

public class DataAnchor {

    /**
     * The mod id for Data Anchor.
     */
    public static final String MOD_ID = "dataanchor";

    /**
     * The logger for Data Anchor.
     */
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final S2CNetworkContainer NETWORK_CONTAINER = S2CNetworkContainer.of(MOD_ID);

    /**
     * Initializes the mod.
     */
    public static void init() {
        registerPacketHandlers();
        QuadTreeNearestPoint quadTreeNearestPoint = new QuadTreeNearestPoint();

        quadTreeNearestPoint.setPoint(new Vec3i(0, 0, 0));
        quadTreeNearestPoint.setPoint(new Vec3i(-10000, 0, -10000));
        quadTreeNearestPoint.setPoint(new Vec3i(10000, 0, 10000));
        quadTreeNearestPoint.setPoint(new Vec3i(-10000, 0, 10000));
        quadTreeNearestPoint.setPoint(new Vec3i(10000, 0, -10000));


        for (int i = 0; i < 120000; i++) {
            RandomSource random = RandomSource.create();
            int x = Mth.randomBetweenInclusive(random, -100000, 100000);
            int z = Mth.randomBetweenInclusive(random, -100000, 100000);
            if (Math.sqrt(x * x + z * z) < 5) {
                continue;
            }
            quadTreeNearestPoint.setPoint(new Vec3i(x, 0, z));
        }



        long currentTimeMillis = System.currentTimeMillis();
        Vec3i nearestPoint = quadTreeNearestPoint.getNearestPoint(new Vec3i(1, 0, 1), Vec3i::distSqr);
        System.out.println("Time taken: " + (System.currentTimeMillis() - currentTimeMillis) + "ms");

        if (nearestPoint != null) {
            System.out.println("Nearest Point: " + nearestPoint);
        } else {
            System.out.println("No nearest point found.");
        }    }

    private static void registerPacketHandlers() {
        NETWORK_CONTAINER.registerPacketHandler(
                new Packet.Handler<>(
                        SyncEntityTrackedDataS2C.class,
                        SyncEntityTrackedDataS2C.TYPE,
                        SyncEntityTrackedDataS2C.STREAM_CODEC,
                        SyncEntityTrackedDataS2C::handle)
        );
        NETWORK_CONTAINER.registerPacketHandler(
                new Packet.Handler<>(
                        SyncLevelChunkTrackedDataS2C.class,
                        SyncLevelChunkTrackedDataS2C.TYPE,
                        SyncLevelChunkTrackedDataS2C.STREAM_CODEC,
                        SyncLevelChunkTrackedDataS2C::handle)
        );
        NETWORK_CONTAINER.registerPacketHandler(
                new Packet.Handler<>(
                        SyncLevelTrackedDataS2C.class,
                        SyncLevelTrackedDataS2C.TYPE,
                        SyncLevelTrackedDataS2C.STREAM_CODEC,
                        SyncLevelTrackedDataS2C::handle
                )
        );
        NETWORK_CONTAINER.registerPacketHandler(
                new Packet.Handler<>(
                        SyncBlockEntityTrackedDataS2C.class,
                        SyncBlockEntityTrackedDataS2C.TYPE,
                        SyncBlockEntityTrackedDataS2C.STREAM_CODEC,
                        SyncBlockEntityTrackedDataS2C::handle
                )
        );

    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}