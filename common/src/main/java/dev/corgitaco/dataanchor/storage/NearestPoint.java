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

import java.util.*;

public interface NearestPoint<T> {
    int[][][] SPIRAL_FAST_2D = spiral2D(32);
    int[][][] SPIRAL_FAST_3D = spiral3D(32);


    void setPoint(Vec3i point, T o);

    void removePoint(Vec3i point);

    @Nullable
    PointData<T> getNearestPointData(Vec3i point, int skip, DistanceFunction distanceFunction, int[] skipDepth);

    default void getNearbyPointDatas(Vec3i point, int maxEntries, Collection<PointData<T>> collector, DistanceFunction distanceFunction) {
         for (int i = 0; i < maxEntries;) {
            PointData<T> foundPoint = getNearestPointData(point, i, distanceFunction, new int[0]);
            if (collector.add(foundPoint)) {
                i++;
            }
        }
    }

    default Collection<PointData<T>> getNearbyPointDatas(Vec3i point, int maxEntries, DistanceFunction distanceFunction) {
        Collection<PointData<T>> pointsWithinRange = new TreeSet<>(Comparator.comparingDouble(value -> distanceFunction.apply(point, value.point)));
        getNearbyPointDatas(point, maxEntries, pointsWithinRange, distanceFunction);
        return pointsWithinRange;
    }

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
        return getPointDataWithinRange(getCenter(min, max), chebyshevDistance(min.getX(), min.getZ(), max.getX(), max.getZ()) / 2D, NearestPoint::chebyshevDistance);
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
        PointData<T> nearestPointData = getNearestPointData(point, 0, distanceFunction, new int[1]);
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

    static int chebyshevDistance(int x1, int y1, int z1, int x2, int y2, int z2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);
        return Math.max(dx, Math.max(dy, dz));
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

    static int[][][] spiral2D(int size) {
        Map<Integer, List<int[]>> distanceMap = new TreeMap<>();
        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                int distance = NearestPoint.chebyshevDistance(0, 0, x, z);
                distanceMap.computeIfAbsent(distance, dist -> new ArrayList<>()).add(new int[]{x, z});
            }
        }

        List<int[][]> offsets = new ArrayList<>();

        for (List<int[]> value : distanceMap.values()) {
            offsets.add(value.toArray(int[][]::new));
        }
        return offsets.toArray(new int[offsets.size()][][]);
    }

    static int[][][] spiral3D(int size) {
        Map<Integer, List<int[]>> distanceMap = new TreeMap<>();
        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    int distance = NearestPoint.chebyshevDistance(0, 0, 0, x, y, z);
                    distanceMap.computeIfAbsent(distance, dist -> new ArrayList<>()).add(new int[]{x, y, z});
                }
            }
        }

        List<int[][]> offsets = new ArrayList<>();

        for (List<int[]> value : distanceMap.values()) {
            offsets.add(value.toArray(int[][]::new));
        }
        return offsets.toArray(new int[offsets.size()][][]);
    }

    @FunctionalInterface
    interface DistanceFunction {
        double apply(Vec3i point1, Vec3i point2);
    }

    record PointData<T>(T value, Vec3i point) {
    }
}
