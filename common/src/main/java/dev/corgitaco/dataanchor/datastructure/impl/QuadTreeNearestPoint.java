/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.datastructure.impl;


import dev.corgitaco.dataanchor.coord.Point;
import dev.corgitaco.dataanchor.datastructure.NearestPoint;
import dev.corgitaco.dataanchor.datastructure.Target;

public class QuadTreeNearestPoint<POINT extends Point> extends QuadTreeNearestPointData<POINT, POINT> {

    public QuadTreeNearestPoint() {
        super();
    }

    public QuadTreeNearestPoint(byte bitShiftScale, byte highestShiftScale, int rowSize) {
        super(bitShiftScale, highestShiftScale, rowSize);
    }

    public Target<POINT, POINT> setPoint(POINT point) {
        return setPoint(point, point);
    }

    public boolean didSetPoint(POINT point) {
        return didSetPoint(point, point);
    }

    @Override
    public <TARGET extends Target<POINT, POINT> & NearestPoint<POINT, POINT>> TARGET targetFactory(POINT point, POINT o) {
        return (TARGET) new PointTarget<>(point);
    }

    @Override
    public NearestPoint<POINT, POINT> makeLeaf(POINT point, POINT o) {
        return new QuadTreeNearestPoint<>((byte) (bitShiftScale + 1), this.highestShiftScale, rowSize());
    }
}
