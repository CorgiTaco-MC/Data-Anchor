package dev.corgitaco.dataanchor.coord;


public interface Point {

    int getX();

    int getY();

    int getZ();

    static int smallestEncompassingPowerOfTwo(int value) {
        int i = value - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    default double distSqr(Point vector) {
        return this.distToLowCornerSqr(vector.getX(), vector.getY(), vector.getZ());
    }

    default double distToCenterSqr(Point position) {
        return this.distToCenterSqr(position.getX(), position.getY(), position.getZ());
    }

    default double distToCenterSqr(double x, double y, double z) {
        double d = (double) this.getX() + (double) 0.5F - x;
        double e = (double) this.getY() + (double) 0.5F - y;
        double f = (double) this.getZ() + (double) 0.5F - z;
        return d * d + e * e + f * f;
    }

    default double distToLowCornerSqr(double x, double y, double z) {
        double d = (double) this.getX() - x;
        double e = (double) this.getY() - y;
        double f = (double) this.getZ() - z;
        return d * d + e * e + f * f;
    }

    @FunctionalInterface
    interface Factory<P extends Point> {
        P create(int x, int y, int z);
    }
}
