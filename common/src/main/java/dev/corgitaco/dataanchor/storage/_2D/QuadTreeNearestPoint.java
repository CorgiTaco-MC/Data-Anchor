package dev.corgitaco.dataanchor.storage._2D;

import net.minecraft.core.Vec3i;

public class QuadTreeNearestPoint extends QuadTreeNearestPointData<Vec3i> {

    public void setPoint(Vec3i point) {
        setPoint(point, point);
    }
}
