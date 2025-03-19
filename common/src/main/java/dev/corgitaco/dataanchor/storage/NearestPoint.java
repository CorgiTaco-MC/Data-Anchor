package dev.corgitaco.dataanchor.storage;

import net.minecraft.core.Vec3i;
import org.jetbrains.annotations.Nullable;

public interface NearestPoint {


    void setPoint(Vec3i point);

    @Nullable
    Vec3i getNearestPoint(Vec3i point);

    void removePoint(Vec3i point);
}
