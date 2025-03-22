package dev.corgitaco.dataanchor.box;

import dev.corgitaco.dataanchor.coord.Point;

public interface PointBox<P extends Point> extends Box {

    Point.Factory<P> factory();


    default P min() {
        return factory().create(minX(), minY(), minZ());
    }

    default P max() {
        return factory().create(minX(), minY(), minZ());
    }

    default P center() {
        return factory().create(minX(), minY(), minZ());
    }

    default boolean intersects(PointBox<?> other) {
        return minX() <= other.maxX() && maxX() >= other.minX() &&
                minY() <= other.maxY() && maxY() >= other.minY() &&
                minZ() <= other.maxZ() && maxZ() >= other.minZ();
    }

    default boolean contains(Point point) {
        return point.getX() >= minX() && point.getX() <= maxX() &&
                point.getY() >= minY() && point.getY() <= maxY() &&
                point.getZ() >= minZ() && point.getZ() <= maxZ();
    }
}
