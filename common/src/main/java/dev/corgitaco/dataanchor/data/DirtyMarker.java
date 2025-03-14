package dev.corgitaco.dataanchor.data;

public interface DirtyMarker {

    void markDirty();

    void clearDirty();
}
