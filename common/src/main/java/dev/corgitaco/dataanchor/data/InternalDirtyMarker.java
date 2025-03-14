package dev.corgitaco.dataanchor.data;

public interface InternalDirtyMarker {

    void dataAnchor$markDirty();

    void dataAnchor$clearDirty();
}
