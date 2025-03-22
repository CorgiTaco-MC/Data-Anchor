package dev.corgitaco.dataanchor.mixin;

import dev.corgitaco.dataanchor.coord.Point;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3i.class)
public abstract class Vec3iMixin implements Point {

    @Shadow
    public abstract int getX();

    @Shadow
    public abstract int getY();

    @Shadow
    public abstract int getZ();

    public int pointX() {
        return getX();
    }

    public int pointY() {
        return getY();
    }

    public int pointZ() {
        return getZ();
    }
}
