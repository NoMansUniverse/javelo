package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
/**
 * @author Robin Bochatay (329724)
 */
public record Waypoint(PointCh via, int closestNodeId) {
}
