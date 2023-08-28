package ch.epfl.javelo.routing;
/**
 * @author Robin Bochatay(329724)
 */
public abstract interface CostFunction {
    /**
     * return the factor by which the length of the edge should be multiplied by to weigh the edge
     * @param nodeId node from which the edge comes from
     * @param edgeId id of the edge
     * @return
     */
    public abstract double costFactor(int nodeId, int edgeId);
}
