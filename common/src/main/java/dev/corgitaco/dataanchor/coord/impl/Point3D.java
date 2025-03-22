package dev.corgitaco.dataanchor.coord.impl;


import dev.corgitaco.dataanchor.coord.Point;

public record Point3D(int x, int y, int z) implements Point {

    @Override
    public int getX() {
        return x();
    }

    @Override
    public int getY() {
        return y();
    }

    @Override
    public int getZ() {
        return z();
    }
}
