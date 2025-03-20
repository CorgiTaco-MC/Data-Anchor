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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface NearestPoint<T> {

    void setPoint(Vec3i point, T o);

    void removePoint(Vec3i point);

    @Nullable
    PointData<T> getNearestPointData(Vec3i point, DistanceFunction distanceFunction);

    Collection<PointData<T>> getPointDataWithinRange(Vec3i point, double radius, DistanceFunction distanceFunction);

    default void removePointsWithinRange(PointData<T> point, int range, DistanceFunction distanceFunction) {
        Collection<PointData<T>> pointsWithinRange = getPointDataWithinRange(point.point, range, distanceFunction);
        for (PointData<T> vec3i : pointsWithinRange) {
            removePoint(vec3i.point);
        }
    }

    boolean isEmpty();

    Collection<PointData<T>> getAllPointData();

    void clear();

    default Collection<PointData<T>> getPointDataInBox(Vec3i min, Vec3i max) {
        return getPointDataWithinRange(getCenter(min, max), chebyshevDistance(min.getX(), min.getY(), max.getX(), max.getY()) / 2D, NearestPoint::chebyshevDistance);
    }

    default Collection<PointData<T>> getPointDataInBox(BlockBox box) {
        return getPointDataInBox(box.min(), box.max());
    }

    default Collection<PointData<T>> getPointDataInBox(BoundingBox box) {
        return getPointDataInBox(new Vec3i(box.minX(), box.minY(), box.minZ()), new Vec3i(box.maxX(), box.maxY(), box.maxZ()));
    }


    default Collection<PointData<T>> getPointDataInBox(Vec3i center, int radius) {
        return getPointDataInBox(new Vec3i(center.getX() - radius, center.getY() - radius, center.getZ() - radius), new Vec3i(center.getX() + radius, center.getY() + radius, center.getZ() + radius));
    }

    default void removePointsInBox(Vec3i min, Vec3i max) {
        Collection<PointData<T>> pointsWithinRange = getPointDataInBox(min, max);
        for (PointData<T> vec3i : pointsWithinRange) {
            removePoint(vec3i.point);
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

    @Nullable
    default Vec3i getNearestPoint(Vec3i point, DistanceFunction distanceFunction) {
        PointData<T> nearestPointData = getNearestPointData(point, distanceFunction);
        if (nearestPointData == null) {
            return null;
        }
        return nearestPointData.point();
    }


    default Collection<Vec3i> getPointsInBox(Vec3i min, Vec3i max) {
        return getVec3is(getPointDataInBox(min, max));
    }

    default Collection<Vec3i> getPointsInBox(Vec3i center, int radius) {
        return getVec3is(getPointDataInBox(center, radius));
    }

    default Collection<Vec3i> getPointsInBox(BlockBox box) {
        return getVec3is(getPointDataInBox(box));
    }

    default Collection<Vec3i> getPointsInBox(BoundingBox box) {
        return getVec3is(getPointDataInBox(box));
    }

    static int chebyshevDistance(Vec3i min, Vec3i max) {
        return chebyshevDistance(min.getX(), min.getZ(), max.getX(), max.getZ());
    }

    static int chebyshevDistance(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        return Math.max(dx, dy);
    }

    private static Vec3i getCenter(Vec3i point1, Vec3i point2) {
        int centerX = (point1.getX() + point2.getX()) / 2;
        int centerY = (point1.getY() + point2.getY()) / 2;
        int centerZ = (point1.getZ() + point2.getZ()) / 2;
        return new Vec3i(centerX, centerY, centerZ);
    }

    private @NotNull List<Vec3i> getVec3is(Collection<PointData<T>> pointsInBox) {
        List<Vec3i> points = new ArrayList<>();
        for (PointData<T> inBox : pointsInBox) {
            points.add(inBox.point());
        }
        return Collections.unmodifiableList(points);
    }

    @FunctionalInterface
    interface DistanceFunction {
        double apply(Vec3i point1, Vec3i point2);
    }

    record PointData<T>(T value, Vec3i point) {
    }
}
