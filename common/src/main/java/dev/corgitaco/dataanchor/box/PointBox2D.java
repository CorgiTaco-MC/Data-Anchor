package dev.corgitaco.dataanchor.box;

import dev.corgitaco.dataanchor.coord.Point;
import dev.corgitaco.dataanchor.coord.impl.Point2D;

public record PointBox2D(Point2D first, Point2D second) implements PointBox<Point2D> {
    @Override
    public Point.Factory<Point2D> factory() {
        return null;
    }

    @Override
    public int minX() {
        return 0;
    }

    @Override
    public int minY() {
        return 0;
    }

    @Override
    public int minZ() {
        return 0;
    }

    @Override
    public int maxX() {
        return 0;
    }

    @Override
    public int maxY() {
        return 0;
    }

    @Override
    public int maxZ() {
        return 0;
    }
}
