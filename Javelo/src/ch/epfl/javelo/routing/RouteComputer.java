package ch.epfl.javelo.routing;


import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import java.util.*;

public final class RouteComputer {
    private Graph graph;
    private CostFunction costfunction;

    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costfunction=costFunction;
    }

    /**
     * compute the best itinerary between startNode and endNode
     * @param startNodeId starting node
     * @param endNodeId node of arrival (has to be different from starting node)
     * @throws IllegalArgumentException if the strating point equal arrival point
     * @return SingleRoute going from startNode to endNode
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId){


        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }


        Preconditions.checkArgument(startNodeId!=endNodeId);

        float[] distance = new float[graph.nodeCount()];
        float d =0;
        int[] predecessors = new int[graph.nodeCount()];
        int edgeId=0;
        int nextNodeId;
        int i=0;
        double effectiveDistance=0;
        WeightedNode nodeInExploration;
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<WeightedNode>(graph.nodeOutDegree(startNodeId));


        en_exploration.add(new WeightedNode(startNodeId,(float) 0));
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        distance[startNodeId] = 0;
        List<Edge> direction;

        while(!en_exploration.isEmpty()){
            do
            {
                nodeInExploration=en_exploration.poll();
            }while(distance[nodeInExploration.nodeId()]==Float.NEGATIVE_INFINITY);


            if(nodeInExploration.nodeId()==endNodeId){
                direction = constructDirection(startNodeId, endNodeId, predecessors);
                return new SingleRoute(direction);
            }
            else {
                i = 0;
                while (i < graph.nodeOutDegree(nodeInExploration.nodeId())) {
                    //récupère le noeud de la i-ème arrète sortant du noeud en exploration
                    edgeId = graph.nodeOutEdgeId(nodeInExploration.nodeId(), i);
                    nextNodeId = graph.edgeTargetNodeId(edgeId);

                    d = (float) (distance[nodeInExploration.nodeId()] +
                            costfunction.costFactor(nodeInExploration.nodeId(), edgeId) * (graph.edgeLength(edgeId)));//LE DERNIER TERME c'est pour A* si on l'enlève on compile le bon chemin(calcul de la distance a vole d'oiseau en plus;
                    if (d < distance[nextNodeId]) {
                        //on remplace dans le tableau la distance par d qui est plus petite
                        distance[nextNodeId] = d;
                        en_exploration.add(new WeightedNode(nextNodeId,
                                (float) (d + (graph.nodePoint(nextNodeId)).distanceTo(graph.nodePoint(endNodeId)))));
                        predecessors[nextNodeId] = nodeInExploration.nodeId();

                    }
                    i++;
                }
                distance[nodeInExploration.nodeId()] = Float.NEGATIVE_INFINITY;
            }
        }
        return  null;
    }

    /**
     * construct recursively the array list of the direction to follow in order to find the path going from startNode to endNode, knowing the array of predecessor
     * @param startNodeId point from which we want to go
     * @param endNodeId finishing point
     * @param predecessors tab in which is contained the predecessor of each node
     * @return list of direction
     */
    private List<Edge> constructDirection(int startNodeId, int endNodeId, int[] predecessors){
        List <Edge> direction= new ArrayList<>();
        //b_temp is the node we want to find its predecessor
        int b_temp=endNodeId;
        int a_temp=predecessors[endNodeId];
        int k=0;
        int edge=0;

        while(b_temp!=startNodeId){
            k=0;
            while(graph.edgeTargetNodeId((edge))!=b_temp && k<graph.nodeOutDegree(a_temp)){
                edge=graph.nodeOutEdgeId(a_temp, k);
                k++;
            }
            direction.add(Edge.of(graph, edge,a_temp,b_temp));
            //recursive reconstruction of the path, in the following way:
            // from b_temp we find a_temp, then when we recovered the edges:
            // b_temp becomes a_temp and a_temp becomes its predecessor
            b_temp=a_temp;
            a_temp=predecessors[a_temp];
        }
        Collections.reverse(direction);
        return direction;
    }
}
