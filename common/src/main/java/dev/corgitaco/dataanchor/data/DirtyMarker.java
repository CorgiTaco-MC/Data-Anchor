package dev.corgitaco.dataanchor.data;

public interface DirtyMarker {

    void dataAnchor$markDirty();

    void dataAnchor$clearDirty();
}
