package dev.corgitaco.dataanchor.levelgen;

import dev.corgitaco.dataanchor.coord.Point;
import dev.corgitaco.dataanchor.coord.impl.Point2D;

import java.util.Set;

public record VillageInfoContext(Set<Point2D> connections, Set<Point2D> failures) {
}
