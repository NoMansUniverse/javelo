package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * @author Upeski Stefan (330129)
 * Class that represents a graph https://en.wikipedia.org/wiki/Graph
 */
public final class Graph {


    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;


    /**
     * Function that allow to construct a graph from a file
     * @param basePath that is the basePath of the file
     * @return a Graph object
     * @throws IOException if the entry/exit of a file doesn't exist
     */

    public static Graph loadFrom(Path basePath) throws IOException  {

         IntBuffer nodes;
        try (FileChannel channel = FileChannel.open(basePath.resolve("nodes.bin"))) {
            nodes = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();

        }

        ByteBuffer sectors;
        try (FileChannel channel = FileChannel.open(basePath.resolve("sectors.bin"))) {
            sectors = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

        }

        ByteBuffer edges;
        try (FileChannel channel = FileChannel.open(basePath.resolve("edges.bin"))) {
            edges = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size());

        }



        IntBuffer profileId;
        try (FileChannel channel = FileChannel.open(basePath.resolve("profile_ids.bin"))) {
            profileId = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();

        }

        ShortBuffer elevations;
        try (FileChannel channel = FileChannel.open(basePath.resolve("elevations.bin"))) {
            elevations = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asShortBuffer();

        }

        LongBuffer attributesArray;
        try (FileChannel channel = FileChannel.open(basePath.resolve("attributes.bin"))) {
            attributesArray = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asLongBuffer();

        }


        List<AttributeSet> attributeSets = new ArrayList<AttributeSet>();

        for (int i = 0; i <attributesArray.capacity() ; i++) {
            // extract le long dans l'array pour le set;
            attributeSets.add(new AttributeSet(attributesArray.get(i)));

        }


        return new Graph(new GraphNodes(nodes), new GraphSectors(sectors), new GraphEdges(edges, profileId, elevations),attributeSets );

    }

    /**
     *  Graph constructor
     *
     * @param nodes of the graph
     * @param sectors contains the info all nodeId of eachSector
     * @param edges of the graph
     * @param attributeSets of all node/edge of the graph
     */

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){

        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);


    }

    /**
     * Function that call the function of  a GraphNode, that count the number of nodes of graph.
     * @return the number total of nodes
     */

    public int nodeCount(){
       return nodes.count();
    };

    /**
     * Function that call the function nodeE and nodeN of a GraphNode with the args nodeId
     * it's allow us to use nodeE and nodeN to create a new Point(nodeE,nodeN)
     * @param nodeId is a nodeId
     * @return PointCh ( Swiss Coordinates)
     */

    public  PointCh nodePoint(int nodeId){
        return  new PointCh(nodes.nodeE(nodeId),nodes.nodeN(nodeId));
    };

    /**
     * Function that take nodeId and return the number that edges left this nodeId
     * @param nodeId is a nodeId
     * @return the number of edge that left this nodeId
     */
    public int nodeOutDegree(int nodeId){
       return nodes.outDegree(nodeId);
    }

    //qui retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId

    /**
     *gives the identity of the edge "edgeIndex" going out of the node "nodeId"
     * @param nodeId node identity we want to find the edge-index-th edge going out from
     * @param edgeIndex index of the edge we want
     * @return int identity of the edge-index-th edge going out from node nodeId
     */

    public int nodeOutEdgeId(int nodeId, int edgeIndex){
       return nodes.edgeId(nodeId,edgeIndex);
    }


    /**
     * Function that find the closestNode from a certain Distance
     * @param point center of the square in PointCh
     * @param searchDistance the search distance
     * @return a int that represent the id of the closestNode from the point
     */

    public int nodeClosestTo(PointCh point, double searchDistance){
          List<GraphSectors.Sector> intersectSector = sectors.sectorsInArea(point,searchDistance);
          int NodeClosest = -1;
          double distance = searchDistance*searchDistance;


        for (int i = 0; i < intersectSector.size()  ; i++) {
            int start = intersectSector.get(i).startNodeId();
            int end = intersectSector.get(i).endNodeId();
            // nodeStartId + length + 1 = endNodeId <=> inverse équations
            for (int j=start; j < end ; j++) {

                if( point.squaredDistanceTo(nodePoint(j)) < distance){

                    distance = point.squaredDistanceTo(nodePoint(j));
                    NodeClosest = j;

                }
            }
        }
        return NodeClosest;
    }

    /**
     * return the node towards which the edge is going
     * @param edgeId edge identity
     * @return int (nodeId of arrival)
     */


   public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    /**
     * check if the edge is going in the same direction as OSM way from which it comes(la voie OSM)
     * @param edgeId edge identity to be checked
     * @return true if it is going reverse (edgeId is negative)
     */



    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);
    }



    /**
     * return the identity of the attributeSet attached to the edge edgeId
     * @param edgeId edge identity of attributeSet identity to be found
     * @return integer that is the identity of the attributeSet attached to the edge
     */

    public AttributeSet edgeAttributes(int edgeId){
        int id = edges.attributesIndex(edgeId);
        return attributeSets.get(id);
    }

    /**
     * return the length of the edge
     * @param edgeId edge identity we want to find length of
     * @return double (Q28_4 in meters)
     */

    public double edgeLength(int edgeId){
       return edges.length(edgeId);
    }

    /**
     * Function that check if a edge has profile and then create a Function sampled
     * @param edgeId edge identity we want
     * @return a function  with sampled if the id of the edge has a profile  or a function that return Double.Nan
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        if(edges.hasProfile(edgeId)){
          return Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId));
        }

        return Functions.constant(Double.NaN);
    }

    /**
     * return the total elevation gained along the edge edgeId
     * @param edgeId edge identity we want to find elevation gain
     * @return double (Q12_4 in meters)
     */

    public double edgeElevationGain(int edgeId){
            return edges.elevationGain((edgeId));
    }

}
