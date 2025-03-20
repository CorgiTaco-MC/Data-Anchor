/*
 * Copyright (c) 2025 Corgi Taco.
 *
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.corgitaco.dataanchor.storage._3D;

import net.minecraft.core.Vec3i;

public class OctreeNearestPoint extends OctreeNearestPointData<Vec3i> {

    public void setPoint(Vec3i point) {
        setPoint(point, point);
    }
}
