package dev.corgitaco.dataanchor.storage;

import net.minecraft.core.Vec3i;

public class QuadTreeNearestPointData extends QuadTreeNearestPointData2D<Vec3i> {

    public void setPoint(Vec3i point) {
        setPoint(point, point);
    }
}
