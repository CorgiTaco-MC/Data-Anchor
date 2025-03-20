/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.storage;

import net.minecraft.core.BlockBox;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface NearestPoint {

    void setPoint(Vec3i point);

    void removePoint(Vec3i point);

    @Nullable
    Vec3i getNearestPoint(Vec3i point, DistanceFunction distanceFunction);

    Collection<Vec3i> getPointsWithinRange(Vec3i point, double radius, DistanceFunction distanceFunction);

    default void removePointsWithinRange(Vec3i point, int range, DistanceFunction distanceFunction) {
        Collection<Vec3i> pointsWithinRange = getPointsWithinRange(point, range, distanceFunction);
        for (Vec3i vec3i : pointsWithinRange) {
            removePoint(vec3i);
        }
    }

    boolean isEmpty();

    Collection<Vec3i> getAllPoints();

    void clear();

    default Collection<Vec3i> getPointsInBox(Vec3i min, Vec3i max) {
        return getPointsWithinRange(getCenter(min, max), chebyshevDistance(min.getX(), min.getY(), max.getX(), max.getY()) / 2D, NearestPoint::chebyshevDistance);
    }

    default Collection<Vec3i> getPointsInBox(BlockBox box) {
        return getPointsInBox(box.min(), box.max());
    }

    default Collection<Vec3i> getPointsInBox(BoundingBox box) {
        return getPointsInBox(new Vec3i(box.minX(), box.minY(), box.minZ()), new Vec3i(box.maxX(), box.maxY(), box.maxZ()));
    }


    default Collection<Vec3i> getPointsInBox(Vec3i center, int radius) {
        return getPointsInBox(new Vec3i(center.getX() - radius, center.getY() - radius, center.getZ() - radius), new Vec3i(center.getX() + radius, center.getY() + radius, center.getZ() + radius));
    }

    default void removePointsInBox(Vec3i min, Vec3i max) {
        Collection<Vec3i> pointsWithinRange = getPointsInBox(min, max);
        for (Vec3i vec3i : pointsWithinRange) {
            removePoint(vec3i);
        }
    }

    default void removePointsInBox(BlockBox box) {
        removePointsInBox(box.min(), box.max());
    }

    default void removePointsInBox(BoundingBox box) {
        removePointsInBox(new Vec3i(box.minX(), box.minY(), box.minZ()), new Vec3i(box.maxX(), box.maxY(), box.maxZ()));
    }

    default void removePointsInBox(Vec3i center, int radius) {
        removePointsInBox(new Vec3i(center.getX() - radius, center.getY() - radius, center.getZ() - radius), new Vec3i(center.getX() + radius, center.getY() + radius, center.getZ() + radius));
    }

    static int chebyshevDistance(Vec3i min, Vec3i max) {
        return chebyshevDistance(min.getX(), min.getZ(), max.getX(), max.getZ());
    }

    static int chebyshevDistance(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        return Math.max(dx, dy);
    }

    default Vec3i getCenter(Vec3i point1, Vec3i point2) {
        int centerX = (point1.getX() + point2.getX()) / 2;
        int centerY = (point1.getY() + point2.getY()) / 2;
        int centerZ = (point1.getZ() + point2.getZ()) / 2;
        return new Vec3i(centerX, centerY, centerZ);
    }

    @FunctionalInterface
    interface DistanceFunction {
        double apply(Vec3i point1, Vec3i point2);
    }
}
