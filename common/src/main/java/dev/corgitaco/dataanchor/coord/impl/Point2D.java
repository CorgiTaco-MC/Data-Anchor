package dev.corgitaco.dataanchor.coord.impl;


import dev.corgitaco.dataanchor.coord.Point;

public record Point2D(int x, int z) implements Point {

    @Override
    public int getX() {
        return x();
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getZ() {
        return z();
    }
}
