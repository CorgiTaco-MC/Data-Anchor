/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.storage._3D;

import dev.corgitaco.dataanchor.storage.NearestPoint;
import net.minecraft.core.Vec3i;

import java.util.*;

public class OctreeNearestPointData<T> implements NearestPoint<T> {

    private final NearestPoint<T>[] leafs = new NearestPoint[2 * 2 * 2];
    private final byte bitShiftScale;
    private final byte highestShiftScale;

    public OctreeNearestPointData(int highestShiftScale) {
        this((byte) 0, (byte) highestShiftScale); // Highest level
    }

    public static <T> OctreeNearestPointData<T> fromSize(int xyzSize) {
        return new OctreeNearestPointData<>((byte) (Integer.SIZE - Integer.numberOfLeadingZeros(xyzSize)));
    }

    public OctreeNearestPointData() {
        this((byte) 0, (byte) 31); // Highest level
    }

    public OctreeNearestPointData(byte bitShiftScale, byte highestShiftScale) {
        this.bitShiftScale = bitShiftScale;
        this.highestShiftScale = highestShiftScale;
        if (bitShiftScale < 0 || bitShiftScale > Integer.SIZE - 1) {
            throw new IllegalArgumentException("bitShiftScale must be between 0 and 31");
        }
    }

    @Override
    public void setPoint(Vec3i point, T o) {
        int x = point.getX();
        int y = point.getY();
        int z = point.getZ();

        int xIndex = getXYZIndex(x);
        int yIndex = getXYZIndex(y);
        int zIndex = getXYZIndex(z);

        setPointRecursively(point, o, getIndex(xIndex, yIndex, zIndex));
    }

    private void setPointRecursively(Vec3i point, T o, int index) {
        if (bitShiftScale == this.highestShiftScale) {
            if (leafs[index] == null) {
                leafs[index] = new Target<>(new PointData<>(o, point));
            }
            return;
        }

        if (leafs[index] == null) {
            leafs[index] = new OctreeNearestPointData<>((byte) (bitShiftScale + 1), this.highestShiftScale);
        }

        leafs[index].setPoint(point, o);
    }

    @Override
    public PointData<T> getNearestPointData(Vec3i point, DistanceFunction distanceFunction) {
        int x = point.getX();
        int y = point.getY();
        int z = point.getZ();

        int xIndex = getXYZIndex(x);
        int yIndex = getXYZIndex(y);
        int zIndex = getXYZIndex(z);

        PointData<T> nearest = null;

        for (int i = 0; i < rowSize(); i++) {
            int[][] distance = SPIRAL_FAST_3D[i];
            for (int[] position : distance) {
                int offsetX = position[0];
                int offsetY = position[1];
                int offsetZ = position[2];

                int offsetXIndex = offsetX + xIndex;
                int offsetYIndex = offsetY + yIndex;
                int offsetZIndex = offsetZ + zIndex;
                int index = getIndex(offsetXIndex, offsetYIndex, offsetZIndex);

                if (offsetXIndex < 0 || offsetXIndex >= rowSize() || offsetYIndex < 0 || offsetYIndex >= rowSize() || offsetZIndex < 0 || offsetZIndex >= rowSize()) {
                    continue;
                }

                NearestPoint<T> offsetNearestPoint = leafs[index];
                if (offsetNearestPoint != null) {
                    PointData<T> offsetNearest = offsetNearestPoint.getNearestPointData(point, distanceFunction);
                    if (offsetNearest != null) {
                        if (nearest == null) {
                            nearest = offsetNearest;
                        } else {
                            if (distanceFunction.apply(nearest.point(), point) > distanceFunction.apply(offsetNearest.point(), point)) {
                                nearest = offsetNearest;
                            }
                        }
                    }
                }
            }

            if (i > 0 && nearest != null) {
                return nearest;
            }
        }
        return nearest;
    }

    @Override
    public Collection<PointData<T>> getPointDataWithinRange(Vec3i point, double radius, DistanceFunction distanceFunction) {
        int x = point.getX();
        int y = point.getY();
        int z = point.getZ();

        Set<PointData<T>> points = new TreeSet<>(Comparator.comparing(pointData -> distanceFunction.apply(point, pointData.point())));

        int xIndex = getXYZIndex(x);
        int yIndex = getXYZIndex(y);
        int zIndex = getXYZIndex(z);

        for (int i = 0; i < rowSize(); i++) {
            int[][] distance = SPIRAL_FAST_3D[i];
            for (int[] position : distance) {
                int offsetX = position[0];
                int offsetY = position[1];
                int offsetZ = position[2];

                int offsetXIndex = offsetX + xIndex;
                int offsetYIndex = offsetY + yIndex;
                int offsetZIndex = offsetZ + zIndex;
                if (offsetXIndex < 0 || offsetXIndex >= rowSize() || offsetYIndex < 0 || offsetYIndex >= rowSize() || offsetZIndex < 0 || offsetZIndex >= rowSize()) {
                    continue;
                }

                NearestPoint<T> offsetNearestPoint = leafs[getIndex(offsetXIndex, offsetYIndex, offsetZIndex)];
                if (offsetNearestPoint != null) {
                    PointData<T> offsetNearest = offsetNearestPoint.getNearestPointData(point, distanceFunction);

                    if (distanceFunction.apply(offsetNearest.point(), point) <= radius) {
                        points.add(offsetNearest);
                    }
                }
            }
        }
        return points;
    }

    private int getXYZIndex(int coord) {
        return (coord >> (this.highestShiftScale - this.bitShiftScale)) & (rowSize() - 1);
    }

    public int rowSize() {
        return (int) Math.cbrt(this.leafs.length);
    }

    private int getIndex(int x, int y, int z) {
        return x * 4 + y * 2 + z;
    }

    @Override
    public boolean isEmpty() {
        for (NearestPoint<T> leaf : leafs) {
            if (leaf != null && !leaf.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Collection<PointData<T>> getAllPointData() {
        List<PointData<T>> points = new ArrayList<>();
        for (NearestPoint<T> leaf : leafs) {
            if (leaf != null && !leaf.isEmpty()) {
                points.addAll(leaf.getAllPointData());
            }
        }

        return Collections.unmodifiableList(points);
    }

    @Override
    public void clear() {
        Arrays.fill(this.leafs, null);
    }

    @Override
    public void removePoint(Vec3i point) {
        int x = point.getX();
        int y = point.getY();
        int z = point.getZ();

        int xIndex = getXYZIndex(x);
        int yIndex = getXYZIndex(y);
        int zIndex = getXYZIndex(z);

        removePointRecursively(point, getIndex(xIndex, yIndex, zIndex));
    }

    private void removePointRecursively(Vec3i point, int index) {
        NearestPoint<T> nearestPoint = leafs[index];
        if (bitShiftScale == this.highestShiftScale) {
            if (nearestPoint != null) {
                leafs[index] = null;
            }
            return;
        }

        if (nearestPoint != null) {
            nearestPoint.removePoint(point);
            if (nearestPoint.isEmpty()) {
                leafs[index] = null;
            }
        }
    }

    public record Target<T>(PointData<T> pointData) implements NearestPoint<T> {

        @Override
        public void setPoint(Vec3i point, T o) {
            throw new IllegalArgumentException("Cannot set lowest level point, use constructor.");
        }

        @Override
        public PointData<T> getNearestPointData(Vec3i point, DistanceFunction distanceFunction) {
            return this.pointData;
        }

        @Override
        public Collection<PointData<T>> getPointDataWithinRange(Vec3i point, double radius, DistanceFunction distanceFunction) {
            return distanceFunction.apply(point, this.pointData.point()) <= radius ? Collections.singleton(this.pointData) : Collections.emptyList();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Collection<PointData<T>> getAllPointData() {
            return Collections.singleton(this.pointData);
        }

        @Override
        public void clear() {
            throw new IllegalArgumentException("Cannot clear lowest level point");
        }

        @Override
        public void removePoint(Vec3i point) {
            throw new IllegalArgumentException("Cannot remove lowest level point");
        }
    }
}