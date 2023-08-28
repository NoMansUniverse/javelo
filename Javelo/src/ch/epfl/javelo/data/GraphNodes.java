package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;
import java.nio.IntBuffer;
/**
 * @author Robin Bochatay(329724)
 */

public record GraphNodes(IntBuffer buffer) {
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;
    private static final int LENGTH_NUMBER_EDGES = 4;


    /**
     * gives the total number of nodes
     * @return integer that is total number of node in the graph
     */
    public int count(){
        return buffer.capacity()/NODE_INTS;
    }

    /**
     * gives the coordinates E of the node "nodeId"
     * @param nodeId node identity Est coordinates to be found of
     * @return double coordinate E
     */
    public double nodeE(int nodeId){
        return Q28_4.asDouble(buffer.get(nodeId*NODE_INTS));
    }
    /**
     * gives the coordinates N of the node "nodeId"
     * @param nodeId node identity North coordinates to be found
     * @return double double coordinate N
     */
    public double nodeN(int nodeId){
        return Q28_4.asDouble(buffer.get(nodeId*NODE_INTS+OFFSET_N));
    }

    /**
     * gives the total number of edges going out of the node given
     * @param nodeId node identity
     * @return int total number of edges going out of node nodeId
     */
    public int outDegree(int nodeId){
        int indexEdge = nodeId*NODE_INTS+OFFSET_OUT_EDGES;
        int bufferValue = buffer.get(indexEdge);
        return Bits.extractUnsigned(bufferValue, Integer.SIZE-LENGTH_NUMBER_EDGES, LENGTH_NUMBER_EDGES);
    }

    /**
     *gives the identity of the edge "edgeIndex" going out of the node "nodeId"
     * @param nodeId node identity we want to find the edge-index-th edge going out from
     * @param edgeIndex index of the edge we want
     * @return int identity of the edge-index-th edge going out from node nodeId
     */
    public int edgeId(int nodeId, int edgeIndex){
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);//controle que l'index de l'arrête n'est pas supérieur au nombre d'arrète sortante

        int indexEdge = nodeId*NODE_INTS+OFFSET_OUT_EDGES;
        int bufferValue = buffer.get(indexEdge);

        return Bits.extractUnsigned(bufferValue, 0, Integer.SIZE-LENGTH_NUMBER_EDGES)+edgeIndex;

    }
}
