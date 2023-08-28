package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile) {

    /**
     *
     * @param graph is the Graph
     * @param edgeId is the id of the edge
     * @param fromNodeId is the start node
     * @param toNodeId is the end node
     * @return an instance of Edge only with args (fromNodeid,toNodeId, edgeId) because it's more simple
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return  new Edge(fromNodeId,toNodeId,
                graph.nodePoint(fromNodeId),
                graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId),
                graph.edgeProfile(edgeId) // faut que tu mettes le truc que t'as fait dans elevationProfile
                );
    }

    // projette le vecteur u sur v

    /**
     * projection of vectors https://en.wikipedia.org/wiki/Vector_projection
     * @param point that is PointCh
     * @return the closestPosition on one Edge , basically it's approximate the Point to get itinary
     */
    public double positionClosestTo(PointCh point){
       return Math2.projectionLength(
               fromPoint.e(),fromPoint().n(),
               toPoint().e(),toPoint().n(),
               point.e(),point.n());
    }

    /**
     *
     * @param position double
     * @return on Point that is on the Edge, with the info position
     */
    public PointCh pointAt(double position){
        double ratio = position / this.length();


       double e =  Math2.interpolate(fromPoint().e(),toPoint.e(),ratio);
       double n = Math2.interpolate(fromPoint().n(),toPoint.n(),ratio);




        return new PointCh(e, n);


    }

    /**
     * function that take position, an edge is 1000m, you give position 500m it's mean that this position is 1/2 on the edge
     * @param position that represent a position
     * @return the elevation of a position on the Edge
     */

    public double elevationAt(double position){

        return profile.applyAsDouble(position);
    }
}
