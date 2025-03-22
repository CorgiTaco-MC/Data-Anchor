/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.datastructure.impl;



import dev.corgitaco.dataanchor.coord.Point;
import dev.corgitaco.dataanchor.datastructure.NearestPoint;
import dev.corgitaco.dataanchor.datastructure.Target;

import java.util.*;

public class QuadTreeNearestPointData<POINT extends Point, VALUE> implements NearestPoint<POINT, VALUE> {

    private final NearestPoint<POINT, VALUE>[] leafs;
    protected final byte bitShiftScale;
    protected final byte highestShiftScale;

    public QuadTreeNearestPointData(int highestShiftScale) {
        this((byte) 0, (byte) highestShiftScale, 2); // Highest level
    }

    public static <POINT extends Point, VALUE> QuadTreeNearestPointData<POINT, VALUE> fromSize(int xzSize) {
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

        int smallestEncompassingPowerOfTwo = Point.smallestEncompassingPowerOfTwo(rowSize);
        if (smallestEncompassingPowerOfTwo != rowSize) {
            System.out.println("rowSize is not a power of two, rounding up to the nearest power of two...");
            rowSize = smallestEncompassingPowerOfTwo;
        }

        this.leafs = new NearestPoint[rowSize * rowSize];
    }

    @Override
    public Target<POINT, VALUE> setPoint(POINT point, VALUE o) {
        int x = point.getX();
        int z = point.getZ();


        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);

        return setPointRecursively(point, o, getIndex(xIndex, zIndex));
    }

    @Override
    public Target<POINT, VALUE> getPoint(POINT point) {
        int x = point.getX();
        int z = point.getZ();


        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);

        int index = getIndex(xIndex, zIndex);
        NearestPoint<POINT, VALUE> nearestPoint = leafs[index];
        if (nearestPoint != null) {
            return nearestPoint.getPoint(point);
        }
        return null;
    }

    private Target<POINT, VALUE> setPointRecursively(POINT point, VALUE o, int index) {
        if (bitShiftScale == this.highestShiftScale) {
            if (leafs[index] == null) {
                leafs[index] = targetFactory(point, o);
                return null;
            } else {
                return (Target<POINT, VALUE>) leafs[index];
            }
        }

        if (leafs[index] == null) {
            leafs[index] = makeLeaf(point, o);
        }

        return leafs[index].setPoint(point, o);
    }

    @Override
    public NearestPoint<POINT, VALUE> makeLeaf(POINT point, VALUE o) {
        return new QuadTreeNearestPointData<>((byte) (bitShiftScale + 1), this.highestShiftScale, rowSize());
    }

    @Override
    public Target<POINT, VALUE> getNearestTarget(Point point, DistanceFunction distanceFunction) {
        int x = point.getX();
        int z = point.getZ();

        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);

        Target<POINT, VALUE> nearest = null;

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

                NearestPoint<POINT, VALUE> offsetNearestPoint = leafs[index];
                if (offsetNearestPoint != null) {
                    Target<POINT, VALUE> offsetNearest = offsetNearestPoint.getNearestTarget(point, distanceFunction);
                    if (offsetNearest != null) {
                        if (nearest == null) {
                            nearest = offsetNearest;
                        } else {
                            double distanceFromNewPoint = distanceFunction.apply(offsetNearest.point(), point);
                            double distanceFromLastPoint = distanceFunction.apply(nearest.point(), point);
                            if (distanceFromLastPoint >= distanceFromNewPoint) {
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
    public void getNearbyTargets(Point point, int maxEntries, Collection<Target<POINT, VALUE>> dataCollector, DistanceFunction distanceFunction) {
        int x = point.getX();
        int z = point.getZ();

        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);

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

                NearestPoint<POINT, VALUE> offsetNearestPoint = leafs[index];
                if (offsetNearestPoint != null) {
                    offsetNearestPoint.getNearbyTargets(point, maxEntries, dataCollector, distanceFunction);
                    if (dataCollector.size() >= maxEntries) {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void getTargetsInBox(Collection<Target<POINT, VALUE>> data, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        int minXIndex = getXZIndex(minX);
        int minZIndex = getXZIndex(minZ);

        int maxXIndex = getXZIndex(maxX);
        int maxZIndex = getXZIndex(maxZ);

        int startXIndex = Math.min(minXIndex, maxXIndex) - 1;
        int startZIndex = Math.min(minZIndex, maxZIndex) - 1;

        int endXIndex = Math.max(minXIndex, maxXIndex) + 1;
        int endZIndex = Math.max(minZIndex, maxZIndex) + 1;


        for (int xIndex = startXIndex; xIndex <= endXIndex; xIndex++) {
            for (int zIndex = startZIndex; zIndex <= endZIndex; zIndex++) {
                if (xIndex < 0 || xIndex >= rowSize() || zIndex < 0 || zIndex >= rowSize()) {
                    continue;
                }

                NearestPoint<POINT, VALUE> offsetNearestPoint = leafs[getIndex(xIndex, zIndex)];
                if (offsetNearestPoint != null) {
                    offsetNearestPoint.getTargetsInBox(data, minX, minY, minZ, maxX, maxY, maxZ);
                }
            }
        }
    }



    @Override
    public void getTargetsWithinRange(Point point, double radius, Collection<Target<POINT, VALUE>> collector, DistanceFunction distanceFunction) {
        int x = point.getX();
        int z = point.getZ();

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

                NearestPoint<POINT, VALUE> offsetNearestPoint = leafs[getIndex(offsetXIndex, offsetZIndex)];
                if (offsetNearestPoint != null) {
                    offsetNearestPoint.getTargetsWithinRange(point, radius, collector, distanceFunction);
                }
            }
        }
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
        for (NearestPoint<POINT, VALUE> leaf : leafs) {
            if (leaf != null && !leaf.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Collection<Target<POINT, VALUE>> getTargets() {
        List<Target<POINT, VALUE>> points = new ArrayList<>();
        for (NearestPoint<POINT, VALUE> leaf : leafs) {
            if (leaf != null && !leaf.isEmpty()) {
                points.addAll(leaf.getTargets());
            }
        }

        return Collections.unmodifiableList(points);
    }

    @Override
    public boolean clear() {
        boolean result = false;
        for (NearestPoint<POINT, VALUE> leaf : this.leafs) {
            if (leaf != null) {
                result = true;
                break;
            }
        }

        Arrays.fill(this.leafs, null);
        return result;
    }

    @Override
    public Target<POINT, VALUE> removePoint(POINT point) {
        int x = point.getX();
        int z = point.getZ();

        int xIndex = getXZIndex(x);
        int zIndex = getXZIndex(z);

        return removePointRecursively(point, getIndex(xIndex, zIndex));
    }

    private Target<POINT, VALUE> removePointRecursively(POINT point, int index) {
        NearestPoint<POINT, VALUE> nearestPoint = leafs[index];
        if (nearestPoint != null) {
            if (bitShiftScale == this.highestShiftScale) {
                leafs[index] = null;
                return (Target<POINT, VALUE>) nearestPoint;
            }

            Target<POINT, VALUE> removedPoint = nearestPoint.removePoint(point);
            if (nearestPoint.isEmpty()) {
                leafs[index] = null;
            }
            return removedPoint;
        }
        return null;
    }
}
