package dev.corgitaco.dataanchor.storage;


import net.minecraft.core.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class QuadTreeNearestPoint2D implements NearestPoint {

    public static final int[][][] SPIRAL_FAST = spiral(32, 32);


    private final NearestPoint[][] leafs = new NearestPoint[2][2];
    private final int bitShiftScale;
    private final int highestShiftScale;

    public QuadTreeNearestPoint2D() {
        this(0, 31); // Highest level
    }

    public QuadTreeNearestPoint2D(int bitShiftScale, int highestShiftScale) {
        this.bitShiftScale = bitShiftScale;
        this.highestShiftScale = highestShiftScale;
        if (bitShiftScale < 0 || bitShiftScale > 31) {
            throw new IllegalArgumentException("bitShiftScale must be between 0 and 31");
        }
    }

    @Override
    public void setPoint(Vec3i point) {
        int x = point.getX();
        int z = point.getZ();


        int xIndex = (x >> (this.highestShiftScale - this.bitShiftScale)) & (this.leafs.length - 1);
        int zIndex = (z >> (this.highestShiftScale - this.bitShiftScale)) & (this.leafs.length - 1);

        setPointRecursively(point, xIndex, zIndex);
    }

    private void setPointRecursively(Vec3i point, int xIndex, int zIndex) {
        if (bitShiftScale == this.highestShiftScale) {
            if (leafs[xIndex][zIndex] == null) {
                leafs[xIndex][zIndex] = new Target(point);
            }
            return;
        }

        if (leafs[xIndex][zIndex] == null) {
            leafs[xIndex][zIndex] = new QuadTreeNearestPoint2D((bitShiftScale + 1), this.highestShiftScale);
        }

        leafs[xIndex][zIndex].setPoint(point);
    }

    @Override
    public Vec3i getNearestPoint(Vec3i point) {
        int x = point.getX();
        int z = point.getZ();

        int xIndex = (x >> (this.highestShiftScale - this.bitShiftScale)) & (this.leafs.length - 1);
        int zIndex = (z >> (this.highestShiftScale - this.bitShiftScale)) & (this.leafs.length - 1);

        Vec3i nearest = null;

        for (int i = 0; i < this.leafs.length; i++) {
            int[][] distance = SPIRAL_FAST[i];
            for (int[] position : distance) {
                int offsetX = position[0];
                int offsetZ = position[1];

                int offsetXIndex = offsetX + xIndex;
                int offsetZIndex = offsetZ + zIndex;
                if (offsetXIndex < 0 || offsetXIndex >= this.leafs.length || offsetZIndex < 0 || offsetZIndex >= this.leafs.length) {
                    continue;
                }

                NearestPoint offsetNearestPoint = leafs[offsetXIndex][offsetZIndex];
                if (offsetNearestPoint != null) {
                    Vec3i offsetNearest = offsetNearestPoint.getNearestPoint(point);
                    if (nearest == null) {
                        nearest = offsetNearest;
                    } else {
                        if (nearest.distManhattan(point) > offsetNearest.distManhattan(point)) {
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
    public void removePoint(Vec3i point) {
        int x = point.getX();
        int z = point.getZ();


        int xIndex = (x >> (this.highestShiftScale - this.bitShiftScale)) & (this.leafs.length - 1);
        int zIndex = (z >> (this.highestShiftScale - this.bitShiftScale)) & (this.leafs.length - 1);

        removePointRecursively(point, xIndex, zIndex);
    }

    private void removePointRecursively(Vec3i point, int xIndex, int zIndex) {
        if (bitShiftScale == this.highestShiftScale) {
            if (leafs[xIndex][zIndex] != null) {
                leafs[xIndex][zIndex] = null;
            }
            return;
        }

        if (leafs[xIndex][zIndex] != null) {
            leafs[xIndex][zIndex] = null;
        }

        leafs[xIndex][zIndex].removePoint(point);
    }


    public record Target(Vec3i point) implements NearestPoint {

        @Override
        public void setPoint(Vec3i point) {
            throw new IllegalArgumentException("Cannot set lowest level point, use constructor.");
        }

        @Override
        public Vec3i getNearestPoint(Vec3i point) {
            return this.point;
        }

        @Override
        public void removePoint(Vec3i point) {
            throw new IllegalArgumentException("Cannot remove lowest level point");
        }
    }

    // Method to generate spiral pattern and return an array of Vec3iitions
    public static int[][][] spiral(int xSize, int ySize) {
        Map<Integer, List<int[]>> distanceMap = new TreeMap<>();
        // Fill the distance map with all Vec3iitions (x, z) from 0 to maxValue
        for (int x = -xSize; x <= xSize; x++) {
            for (int z = -ySize; z <= ySize; z++) {
                int distSquared = chebyshevDistance(0, 0, x, z); // Distance squared to avoid floating-point math
                // Add the Vec3iition (x, z) to the list of Vec3iitions for the given distance
                distanceMap.computeIfAbsent(distSquared, k -> new ArrayList<>()).add(new int[]{x, z});
            }
        }

        List<int[][]> Vec3iitionOffsets = new ArrayList<>();

        for (List<int[]> value : distanceMap.values()) {
            Vec3iitionOffsets.add(value.toArray(int[][]::new));
        }
        return Vec3iitionOffsets.toArray(new int[Vec3iitionOffsets.size()][][]);
    }

    public static int chebyshevDistance(int x1, int y1, int x2, int y2) {
        // Calculate the absolute differences along each axis
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        // Return the maximum of the two differences
        return Math.max(dx, dy);
    }
}
