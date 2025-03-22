package dev.corgitaco.dataanchor.levelgen;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class PathingChunkData {
    @Nullable
    private int[] surfaceHeight;
    private final int[] surfaceBiomes;

    public PathingChunkData(int[] surfaceBiomes) {
        this(null, surfaceBiomes);
    }

    public PathingChunkData(int[] surfaceHeight, int[] surfaceBiomes) {
        this.surfaceHeight = surfaceHeight;
        this.surfaceBiomes = surfaceBiomes;
    }

    @Nullable
    public int[] surfaceHeight() {
        return surfaceHeight;
    }

    public void setSurfaceHeight(int[] surfaceHeight) {
        this.surfaceHeight = surfaceHeight;
    }

    public int[] surfaceBiomes() {
        return surfaceBiomes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PathingChunkData) obj;
        return Objects.equals(this.surfaceHeight, that.surfaceHeight) &&
                this.surfaceBiomes == that.surfaceBiomes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(surfaceHeight, surfaceBiomes);
    }

    @Override
    public String toString() {
        return "PathingChunkData[" +
                "surfaceHeight=" + surfaceHeight + ", " +
                "surfaceBiomes=" + surfaceBiomes + ']';
    }

}
