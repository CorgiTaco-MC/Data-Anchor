package dev.corgitaco.dataanchor.storage;

import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface NearestPoint {


    void setPoint(Vec3i point);

    @Nullable
    Vec3i getNearestPoint(Vec3i point, DistanceFunction distanceFunction);

    Collection<Vec3i> getPointsWithinRange(Vec3i point, int range, DistanceFunction distanceFunction);

    default void removePointsWithinRange(Vec3i point, int range, DistanceFunction distanceFunction) {
        Collection<Vec3i> pointsWithinRange = getPointsWithinRange(point, range, distanceFunction);
        for (Vec3i vec3i : pointsWithinRange) {
            removePoint(vec3i);
        }
    }

    boolean isEmpty();

//    Collection<Vec3i> getPointsInBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ);
//
//
//    default Collection<Vec3i> getPointsInBox(Vec3i min, Vec3i max) {
//        return getPointsInBox(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
//    }
//
//    default Collection<Vec3i> getPointsInBox(Vec3i center, int radius) {
//        return getPointsInBox(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius);
//    }

    void removePoint(Vec3i point);


    @FunctionalInterface
    interface DistanceFunction {
        double apply(Vec3i point1, Vec3i point2);
    }
}
