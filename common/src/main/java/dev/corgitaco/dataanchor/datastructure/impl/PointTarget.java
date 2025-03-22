package dev.corgitaco.dataanchor.datastructure.impl;

import dev.corgitaco.dataanchor.coord.Point;
import dev.corgitaco.dataanchor.datastructure.NearestPoint;
import dev.corgitaco.dataanchor.datastructure.Target;

import java.util.Collection;
import java.util.Collections;

public record PointTarget<POINT extends Point>(
        POINT point
) implements NearestPoint<POINT, POINT>, Target<POINT, POINT> {

    @Override
    public Target<POINT, POINT> setPoint(POINT point, POINT o) {
        throw new IllegalArgumentException("Cannot set lowest level point, use constructor.");
    }

    @Override
    public Target<POINT, POINT> getPoint(POINT point) {
        return this;
    }

    @Override
    public Target<POINT, POINT> removePoint(POINT point) {
        throw new IllegalArgumentException("Cannot remove lowest level point.");
    }

    @Override
    public NearestPoint<POINT, POINT> makeLeaf(POINT point, POINT o) {
        throw new IllegalArgumentException("Cannot create leaf on low level point, use constructor.");
    }

    @Override
    public Target<POINT, POINT> getNearestTarget(Point point, DistanceFunction distanceFunction) {
        return this;
    }

    @Override
    public void getNearbyTargets(Point point, int maxEntries, Collection<Target<POINT, POINT>> collector, DistanceFunction distanceFunction) {
        if (collector.size() < maxEntries) {
            collector.add(this);
        }
    }

    @Override
    public void getTargetsWithinRange(Point point, double radius, Collection<Target<POINT, POINT>> collector, DistanceFunction distanceFunction) {
        if (distanceFunction.apply(point, this.point()) <= radius) {
            collector.add(this);
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Collection<Target<POINT, POINT>> getTargets() {
        return Collections.singleton(this);
    }

    @Override
    public POINT value() {
        return this.point;
    }

    @Override
    public boolean clear() {
        throw new IllegalArgumentException("Cannot clear lowest level point");
    }

    @Override
    public void getTargetsInBox(Collection<Target<POINT, POINT>> data, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        boolean checkX = this.point.getX() >= minX && this.point.getX() <= maxX;
        boolean checkY = this.point.getY() >= minY && this.point.getY() <= maxY;
        boolean checkZ = this.point.getZ() >= minZ && this.point.getZ() <= maxZ;

        if (checkX && checkY && checkZ) {
            data.add(this);
        }
    }
}