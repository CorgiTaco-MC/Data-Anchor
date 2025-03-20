/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.storage;


import net.minecraft.core.Vec3i;

import java.util.*;

public class QuadTreeNearestPoint2D implements NearestPoint {

    public static final int[][][] SPIRAL_FAST = spiral(32, 32);

    private final NearestPoint[] leafs = new NearestPoint[2 * 2];
    private final byte bitShiftScale;
    private final byte highestShiftScale;

    public QuadTreeNearestPoint2D(int highestShiftScale) {
        this((byte) 0, (byte) highestShiftScale); // Highest level
    }

    public static QuadTreeNearestPoint2D fromSize(int xzSize) {
        return new QuadTreeNearestPoint2D((byte) (Integer.SIZE - Integer.numberOfLeadingZeros(xzSize)));
    }

    public QuadTreeNearestPoint2D() {
        this((byte) 0, (byte) 31); // Highest level
    }

    public QuadTreeNearestPoint2D(byte bitShiftScale, byte highestShiftScale) {
        this.bitShiftScale = bitShiftScale;
        this.highestShiftScale = highestShiftScale;
        if (bitShiftScale < 0 || bitShiftScale > Integer.SIZE - 1) {
            throw new IllegalArgumentException("bitShiftScale must be between 0 and 31");
        }
    }

    @Override
    public void setPoint(Vec3i point) {
        int x = point.getX();
        int z = point.getZ();


        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);

        setPointRecursively(point, getIndex(xIndex, zIndex));
    }

    private void setPointRecursively(Vec3i point, int index) {
        if (bitShiftScale == this.highestShiftScale) {
            if (leafs[index] == null) {
                leafs[index] = new Target(point);
            }
            return;
        }

        if (leafs[index] == null) {
            leafs[index] = new QuadTreeNearestPoint2D((byte) (bitShiftScale + 1), this.highestShiftScale);
        }

        leafs[index].setPoint(point);
    }

    @Override
    public Vec3i getNearestPoint(Vec3i point, DistanceFunction distanceFunction) {
        int x = point.getX();
        int z = point.getZ();

        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);

        Vec3i nearest = null;

        for (int i = 0; i < rowSize(); i++) {
            int[][] distance = SPIRAL_FAST[i];
            for (int[] position : distance) {
                int offsetX = position[0];
                int offsetZ = position[1];

                int offsetXIndex = offsetX + xIndex;
                int offsetZIndex = offsetZ + zIndex;
                int index = getIndex(offsetXIndex, offsetZIndex);

                if (offsetXIndex < 0 || offsetXIndex >= rowSize() || offsetZIndex < 0 || offsetZIndex >= rowSize()) {
                    continue;
                }

                NearestPoint offsetNearestPoint = leafs[index];
                if (offsetNearestPoint != null) {
                    Vec3i offsetNearest = offsetNearestPoint.getNearestPoint(point, distanceFunction);
                    if (nearest == null) {
                        nearest = offsetNearest;
                    } else {
                        if (distanceFunction.apply(nearest, point) > distanceFunction.apply(offsetNearest, point)) {
                            nearest = offsetNearest;
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
    public Collection<Vec3i> getPointsWithinRange(Vec3i point, double radius, DistanceFunction distanceFunction) {
        int x = point.getX();
        int z = point.getZ();

        Set<Vec3i> points = new TreeSet<>(Comparator.comparing(point::distSqr));

        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);


        for (int i = 0; i < rowSize(); i++) {
            int[][] distance = SPIRAL_FAST[i];
            for (int[] position : distance) {
                int offsetX = position[0];
                int offsetZ = position[1];

                int offsetXIndex = offsetX + xIndex;
                int offsetZIndex = offsetZ + zIndex;
                if (offsetXIndex < 0 || offsetXIndex >= rowSize() || offsetZIndex < 0 || offsetZIndex >= rowSize()) {
                    continue;
                }

                NearestPoint offsetNearestPoint = leafs[getIndex(offsetXIndex, offsetZIndex)];
                if (offsetNearestPoint != null) {
                    Vec3i offsetNearest = offsetNearestPoint.getNearestPoint(point, distanceFunction);

                    if (distanceFunction.apply(offsetNearest, point) <= radius) {
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
        return this.leafs.length / 2;
    }


    private int getIndex(int x, int z) {
        return x * 2 + z;
    }

    @Override
    public boolean isEmpty() {
        for (NearestPoint leaf : leafs) {
            if (leaf != null && !leaf.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Collection<Vec3i> getAllPoints() {
        List<Vec3i> points = new ArrayList<>();
        for (NearestPoint leaf : leafs) {
            if (leaf != null && !leaf.isEmpty()) {
                points.addAll(leaf.getAllPoints());
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


    public record Target(Vec3i point) implements NearestPoint {

        @Override
        public void setPoint(Vec3i point) {
            throw new IllegalArgumentException("Cannot set lowest level point, use constructor.");
        }

        @Override
        public Vec3i getNearestPoint(Vec3i point, DistanceFunction distanceFunction) {
            return this.point;
        }

        @Override
        public Collection<Vec3i> getPointsWithinRange(Vec3i point, double radius, DistanceFunction distanceFunction) {
            return distanceFunction.apply(point, this.point) <= radius ? Collections.singleton(this.point) : Collections.emptyList();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Collection<Vec3i> getAllPoints() {
            return Collections.singleton(point);
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

    public static int[][][] spiral(int xSize, int ySize) {
        Map<Integer, List<int[]>> distanceMap = new TreeMap<>();
        for (int x = -xSize; x <= xSize; x++) {
            for (int z = -ySize; z <= ySize; z++) {
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


}
