package dev.corgitaco.dataanchor.storage;


import net.minecraft.core.Vec3i;

import java.util.*;

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
    public Vec3i getNearestPoint(Vec3i point, DistanceFunction distanceFunction) {
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
    public Collection<Vec3i> getPointsWithinRange(Vec3i point, int range, DistanceFunction distanceFunction) {
        int x = point.getX();
        int z = point.getZ();

        Set<Vec3i> points = new TreeSet<>(Comparator.comparing(point::distSqr));

        int xIndex = (x >> (this.highestShiftScale - this.bitShiftScale)) & (this.leafs.length - 1);
        int zIndex = (z >> (this.highestShiftScale - this.bitShiftScale)) & (this.leafs.length - 1);


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
                    Vec3i offsetNearest = offsetNearestPoint.getNearestPoint(point, distanceFunction);

                    if (distanceFunction.apply(offsetNearest, point) <= range) {
                        points.add(offsetNearest);
                    }
                }
            }
        }
        return points;
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
        public Vec3i getNearestPoint(Vec3i point, DistanceFunction distanceFunction) {
            return this.point;
        }

        @Override
        public Collection<Vec3i> getPointsWithinRange(Vec3i point, int range, DistanceFunction distanceFunction) {
            return Collections.singleton(this.point);
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
                int distSquared = chebyshevDistance(0, 0, x, z);
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
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        return Math.max(dx, dy);
    }
}
