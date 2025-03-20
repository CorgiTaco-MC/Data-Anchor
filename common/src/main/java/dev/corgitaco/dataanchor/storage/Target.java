package dev.corgitaco.dataanchor.storage;

import net.minecraft.core.Vec3i;

import java.util.Collection;
import java.util.Collections;

public record Target<T>(PointData<T> pointData) implements NearestPoint<T> {

    @Override
    public void setPoint(Vec3i point, T o) {
        throw new IllegalArgumentException("Cannot set lowest level point, use constructor.");
    }

    @Override
    public PointData<T> getNearestPointData(Vec3i point, int skip, DistanceFunction distanceFunction, int[] skipDepth) {
        if (skipDepth[0] < skip) {
            skipDepth[0]++;
            return null;
        }

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