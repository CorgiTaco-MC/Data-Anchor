/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.storage._2D;


import dev.corgitaco.dataanchor.DataAnchor;
import dev.corgitaco.dataanchor.storage.NearestPoint;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;

import java.util.*;

public class QuadTreeNearestPointData<T> implements NearestPoint<T> {


    private final NearestPoint<T>[] leafs;
    private final byte bitShiftScale;
    private final byte highestShiftScale;

    public QuadTreeNearestPointData(int highestShiftScale) {
        this((byte) 0, (byte) highestShiftScale, 2); // Highest level
    }

    public static <T> QuadTreeNearestPointData<T> fromSize(int xzSize) {
        return new QuadTreeNearestPointData<>((byte) (Integer.SIZE - Integer.numberOfLeadingZeros(xzSize)));
    }

    public QuadTreeNearestPointData() {
        this((byte) 0, (byte) 31, 2); // Highest level
    }

    public QuadTreeNearestPointData(byte bitShiftScale, byte highestShiftScale, int rowSize) {
        this.bitShiftScale = bitShiftScale;
        this.highestShiftScale = highestShiftScale;
        if (bitShiftScale < 0 || bitShiftScale > Integer.SIZE - 1) {
            throw new IllegalArgumentException("bitShiftScale must be between 0 and 31");
        }

        if (rowSize < 2) {
            throw new IllegalArgumentException("rowSize must be greater than 1");
        }

        int smallestEncompassingPowerOfTwo = Mth.smallestEncompassingPowerOfTwo(rowSize);
        if (smallestEncompassingPowerOfTwo != rowSize) {
            DataAnchor.LOGGER.warn("rowSize is not a power of two, rounding up to the nearest power of two...");
            rowSize = smallestEncompassingPowerOfTwo;
        }

        this.leafs = new NearestPoint[rowSize * rowSize];
    }

    @Override
    public void setPoint(Vec3i point, T o) {
        int x = point.getX();
        int z = point.getZ();


        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);

        setPointRecursively(point, o, getIndex(xIndex, zIndex));
    }

    private void setPointRecursively(Vec3i point, T o, int index) {
        if (bitShiftScale == this.highestShiftScale) {
            if (leafs[index] == null) {
                leafs[index] = new Target<>(new PointData<>(o, point));
            }
            return;
        }

        if (leafs[index] == null) {
            leafs[index] = new QuadTreeNearestPointData((byte) (bitShiftScale + 1), this.highestShiftScale, this.leafs.length);
        }

        leafs[index].setPoint(point, o);
    }

    @Override
    public PointData<T> getNearestPointData(Vec3i point, DistanceFunction distanceFunction) {
        int x = point.getX();
        int z = point.getZ();

        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);

        PointData<T> nearest = null;

        for (int i = 0; i < rowSize(); i++) {
            int[][] distance = SPIRAL_FAST_2D[i];
            for (int[] position : distance) {
                int offsetX = position[0];
                int offsetZ = position[1];

                int offsetXIndex = offsetX + xIndex;
                int offsetZIndex = offsetZ + zIndex;
                int index = getIndex(offsetXIndex, offsetZIndex);

                if (offsetXIndex < 0 || offsetXIndex >= rowSize() || offsetZIndex < 0 || offsetZIndex >= rowSize()) {
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
        int z = point.getZ();

        Set<PointData<T>> points = new TreeSet<>(Comparator.comparing(pointData -> distanceFunction.apply(point, pointData.point())));

        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);


        for (int i = 0; i < rowSize(); i++) {
            int[][] distance = SPIRAL_FAST_2D[i];
            for (int[] position : distance) {
                int offsetX = position[0];
                int offsetZ = position[1];

                int offsetXIndex = offsetX + xIndex;
                int offsetZIndex = offsetZ + zIndex;
                if (offsetXIndex < 0 || offsetXIndex >= rowSize() || offsetZIndex < 0 || offsetZIndex >= rowSize()) {
                    continue;
                }

                NearestPoint<T> offsetNearestPoint = leafs[getIndex(offsetXIndex, offsetZIndex)];
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

    private int getXZIndex(int coord) {
        return (coord >> (this.highestShiftScale - this.bitShiftScale)) & (rowSize() - 1);
    }

    public int rowSize() {
        return this.leafs.length >> 1;
    }


    private int getIndex(int x, int z) {
        return x * rowSize() + z;
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
        int z = point.getZ();

        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);

        removePointRecursively(point, getIndex(xIndex, zIndex));
    }

    private void removePointRecursively(Vec3i point, int index) {
        NearestPoint nearestPoint = leafs[index];
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
