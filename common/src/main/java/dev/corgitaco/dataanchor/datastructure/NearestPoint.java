/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.datastructure;

import dev.corgitaco.dataanchor.box.PointBox;
import dev.corgitaco.dataanchor.coord.Point;
import dev.corgitaco.dataanchor.coord.impl.Point3D;
import dev.corgitaco.dataanchor.datastructure.impl.StandardTarget;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface NearestPoint<POINT extends Point, VALUE> {
    int[][][] SPIRAL_FAST_2D = spiral2D(32);
    int[][][] SPIRAL_FAST_3D = spiral3D(32);

    Point NEGATIVE_INFINITE = new Point() {
        @Override
        public int getX() {
            return Integer.MIN_VALUE;
        }

        @Override
        public int getY() {
            return Integer.MIN_VALUE;
        }

        @Override
        public int getZ() {
            return Integer.MIN_VALUE;
        }
    };

    NearestPoint<POINT, VALUE> makeLeaf(POINT point, VALUE o);

    Target<POINT, VALUE> setPoint(POINT point, VALUE o);

    @Nullable
    Target<POINT, VALUE> getPoint(POINT point);

    Target<POINT, VALUE> removePoint(POINT point);

    Target<POINT, VALUE> getNearestTarget(Point point, DistanceFunction distanceFunction);

    void getNearbyTargets(Point point, int maxEntries, Collection<Target<POINT, VALUE>> collector, DistanceFunction distanceFunction);

    void getTargetsWithinRange(Point point, double radius, Collection<Target<POINT, VALUE>> collector, DistanceFunction distanceFunction);

    boolean isEmpty();

    Collection<Target<POINT, VALUE>> getTargets();

    boolean clear();

    /**
     * @return true if the point changed
     */
    default boolean didSetPoint(POINT point, VALUE o) {
        Target<POINT, VALUE> pointvalueTarget = setPoint(point, o);
        if (pointvalueTarget == null) {
            return true;
        }
        return !pointvalueTarget.value().equals(o) && !pointvalueTarget.point().equals(point);
    }

    @Nullable
    default <TARGET extends Target<POINT, VALUE> & NearestPoint<POINT, VALUE>> TARGET targetFactory(POINT point, VALUE o) {
        return (TARGET) new StandardTarget<>(point, o);
    }

    default Collection<Target<POINT, VALUE>> getNearbyTargets(Point point, int maxEntries, DistanceFunction distanceFunction) {
        List<Target<POINT, VALUE>> pointsWithinRange = new ArrayList<>();
        getNearbyTargets(point, maxEntries, pointsWithinRange, distanceFunction);
        return pointsWithinRange.stream().sorted(targetSorted(point)).toList();
    }


    default void removePointsWithinRange(Target<POINT, VALUE> point, int range, DistanceFunction distanceFunction) {
        Collection<Target<POINT, VALUE>> pointsWithinRange = new ArrayList<>();
        getTargetsWithinRange(point.point(), range, pointsWithinRange, distanceFunction);
        for (Target<POINT, VALUE> vec3i : pointsWithinRange) {
            removePoint(vec3i.point());
        }
    }

    void getTargetsInBox(Collection<Target<POINT, VALUE>> data, int minX, int minY, int minZ, int maxX, int maxY, int maxZ);

    default Collection<Target<POINT, VALUE>> getTargetsInBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        Collection<Target<POINT, VALUE>> pointsWithinRange = new ArrayList<>();
        getTargetsInBox(pointsWithinRange, minX, minY, minZ, maxX, maxY, maxZ);
        return pointsWithinRange.stream().sorted(Comparator.comparingDouble(value -> value.point().distSqr(NEGATIVE_INFINITE))).toList();
    }

    default Collection<Target<POINT, VALUE>> getTargetsInBox(Point min, Point max) {
        return getTargetsInBox(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    default Collection<Target<POINT, VALUE>> getTargetsInBox(PointBox<POINT> box) {
        return getTargetsInBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
    }

    default Collection<Target<POINT, VALUE>> getTargetsInBox(Point center, int radius) {
        return getTargetsInBox(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius);
    }

    default void removePointsInBox(Point min, Point max) {
        Collection<Target<POINT, VALUE>> pointsWithinRange = getTargetsInBox(min, max);
        for (Target<POINT, VALUE> vec3i : pointsWithinRange) {
            removePoint(vec3i.point());
        }
    }

    default void removePointsInBox(PointBox<?> box) {
        removePointsInBox(box.min(), box.max());
    }

    default void removePointsInBox(Point center, int radius) {
        removePointsInBox(new Point3D(center.getX() - radius, center.getY() - radius, center.getZ() - radius), new Point3D(center.getX() + radius, center.getY() + radius, center.getZ() + radius));
    }

    @Nullable
    default <OP extends Point> POINT getNearestPoint(OP point, DistanceFunction distanceFunction) {
        Target<POINT, VALUE> nearestTarget = getNearestTarget(point, distanceFunction);
        if (nearestTarget == null) {
            return null;
        }
        return nearestTarget.point();
    }


    default Collection<POINT> getPointsInBox(POINT min, POINT max) {
        return getPoints(getTargetsInBox(min, max));
    }

    default Collection<POINT> getPointsInBox(Point center, int radius) {
        return getPoints(getTargetsInBox(center, radius));
    }

    static int chebyshevDistance(Point min, Point max) {
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

    private Point getCenter(Point point1, Point point2) {
        int centerX = (point1.getX() + point2.getX()) / 2;
        int centerY = (point1.getY() + point2.getY()) / 2;
        int centerZ = (point1.getZ() + point2.getZ()) / 2;
        return new Point3D(centerX, centerY, centerZ);
    }

    private List<POINT> getPoints(Collection<Target<POINT, VALUE>> pointsInBox) {
        List<POINT> points = new ArrayList<>();
        for (Target<POINT, VALUE> inBox : pointsInBox) {
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


    static <P extends Point, T> Comparator<Target<P, T>> targetSorted(Point point) {
        return Comparator.comparingDouble((Target<P, T> value) -> value.point().distSqr(NEGATIVE_INFINITE)).thenComparingDouble(value -> value.point().distSqr(point));
    }

    static <P extends Point> Comparator<Point> pointsSorted(Point point) {
        return Comparator.comparingDouble((Point value) -> value.distSqr(NEGATIVE_INFINITE)).thenComparingDouble(value -> value.distSqr(point));
    }

    @FunctionalInterface
    interface DistanceFunction {
        double apply(Point point1, Point point2);
    }
}
