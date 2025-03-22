package dev.corgitaco.dataanchor.datastructure;


import dev.corgitaco.dataanchor.coord.Point;

import java.util.function.Supplier;

public interface Target<POINT extends Point, VALUE> extends Supplier<VALUE> {

    POINT point();

    VALUE value();

    @Override
    default VALUE get() {
        return value();
    }
}