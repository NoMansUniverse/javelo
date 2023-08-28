package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Upeski Stefan (330129)
 */

public class SingleRoute implements Route {

    private final List<Edge> edges;

    /**
     * constrcutor of a SingleRoute
     * @param edges all the edges of intinary
     * @throws IllegalArgumentException if the length of the array of edges is egal to 0
     */
    public SingleRoute(List<Edge> edges){

        Preconditions.checkArgument(edges.size() >0);

        this.edges = List.copyOf(edges);
    } // qui retourne l'itinéraire simple composé des arêtes données, ou lève IllegalArgumentException si la liste d'arêtes est vide.


    private double[] makeOrderedArray(List<Edge> edges){


        double[] orderedArray = new double[points().size()];




        orderedArray[0] = 0;
        double countTotalLength = 0;

        for (int i = 0; i < edges.size(); i++) {

            countTotalLength = countTotalLength + edges.get(i).length();

            orderedArray[i+1] =  countTotalLength;

        }



        return orderedArray;


    }



    private PointCh handleBinarySearchEdgeIndex(double binarySearchResult,double[] orderedPositionArray,double Position){



        if(binarySearchResult < 0){
            if(binarySearchResult == -1.0){
                return edges.get(0).fromPoint();
            }

            if( binarySearchResult == (-1* points().size())-1){




                return edges.get(edges().size()-1).toPoint();

            }

            else {



                double differencePosition = Position - orderedPositionArray[(int)(-1*binarySearchResult -2)];




                return   edges.get((int) (-1*binarySearchResult -2)).pointAt(differencePosition);
            }
        }

        else{
            if (binarySearchResult == edges.size()){
               return edges.get((int) binarySearchResult-1).toPoint();
            }
            return edges.get((int) binarySearchResult).fromPoint();
        }




    }

    private int handleBinarySearchEdgeNode(double binarySearchResult,double[] orderedPositionArray,double Position){


        if(binarySearchResult < 0){
            if(binarySearchResult == -1.0){
                return edges.get(0).fromNodeId();
            }

            if( binarySearchResult == (-1* points().size())-1){




                return edges.get(edges().size()-1).toNodeId();

            }

            else {



                double differencePosition = Position - orderedPositionArray[(int)(-1*binarySearchResult -2)];


                if(differencePosition/edges.get((int) (-1*binarySearchResult -2)).length() <= 0.5 ){
                    return edges.get((int) (-1*binarySearchResult -2)).fromNodeId();
                }

                else{
                    return edges.get((int) (-1*binarySearchResult -2)).toNodeId();
                }

            }
        }

        else{
            if (binarySearchResult == edges.size()){
                return edges.get((int) binarySearchResult-1).toNodeId();
            }
            return edges.get((int) binarySearchResult).fromNodeId();
        }




    }


    private double handleBinarySearchEdgeEleveation(double binarySearchResult,double[] orderedPositionArray,double Position){


        if(binarySearchResult < 0){
            if(binarySearchResult == -1.0){
                return edges.get(0).elevationAt(0);
            }

            if( binarySearchResult == (-1* points().size())-1){




                return edges.get(edges().size()-1).elevationAt( edges.get((edges.size()-1)).length());

            }

            else {



                double differencePosition = Position - orderedPositionArray[(int)(-1*binarySearchResult -2)];




                return   edges.get((int) (-1*binarySearchResult -2)).elevationAt(differencePosition);
            }
        }

        else{
            if (binarySearchResult == edges.size()){
                return edges.get((int) binarySearchResult-1).elevationAt(edges.get(edges.size()-1).length());
            }
            return edges.get((int) binarySearchResult).elevationAt(0);
        }




    }


    /**
     *
     * @param position the position on a itinerary
     * @return 0 always for each singleRoute
     */
    @Override
    public int indexOfSegmentAt(double position) {

        return 0;
    }

    /**
     * compute the total length of itinary
     * @return the totalLength;
     */
    @Override
    public double length() {
        double count = 0;
        for (int i = 0; i <edges().size() ; i++) {
            count = count + edges().get(i).length();
        }
        return count;
    }

    /**
     *  Method that return all the edges of the itinary
     * @return all the edges of the itinary
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * List de PointCh qui contient l'emsemble des points se trouvant sur un itinéaire
     * @return a List that return distinguish Points that are on a SingleRoute
     */
    @Override
    public List<PointCh> points() {

        ArrayList<PointCh> allPoint = new ArrayList<>();
        // on boucle sur l'array d'edge
        for (int i = 0; i <=edges.size()  ; i++) {
            // on fait qu'il ait pas de doublon.
            if ( i == edges.size()) {
                allPoint.add(edges.get(edges.size()-1).toPoint());


            }

            else{
                allPoint.add(edges.get(i).fromPoint());
            }
        }
        return allPoint;
    }

    /**
     * Function take a double position , this is a position on the Single and give the point on the SingleRoute that is a that position
     * @param position the position on a intinary
     * @return a PointCh on Single that is a that position
     */

    @Override
    public PointCh pointAt(double position) {
        // on crée un array qui contient une size de l'ensemble ps voir explication dans la feuille de route du prof pour comprendre cette partie
        double[] orderedArray = makeOrderedArray(edges);
        position= Math2.clamp(0,position,length());

        PointCh differencePosition = handleBinarySearchEdgeIndex(Arrays.binarySearch(orderedArray,position),orderedArray,position);


        return differencePosition;
    }

    /**
     * Function take a double position , this is a position on the Single and give the point on the SingleRoute that is a that position
     * @param position
     * @return the eleveation of that position
     */

    @Override
    public double elevationAt(double position) {



        double[] orderedArray = makeOrderedArray(edges);

        double differencePosition = handleBinarySearchEdgeEleveation(Arrays.binarySearch(orderedArray,position),orderedArray,position);



        return differencePosition;
    }

    /**
     * Function take a double position , this is a position on the Single and give the closest node on the SingleRoute that is a that position
     * @param position the position on a intinery
     * @return a int NodeId
     */

    @Override
    public int nodeClosestTo(double position) {

        double[] orderedArray = makeOrderedArray(edges);

        int differencePosition = handleBinarySearchEdgeNode(Arrays.binarySearch(orderedArray,position),orderedArray,position);



        return differencePosition;

    }

    /**
     *  Function that take point on arg , and basically to geometrical stuff like projection to find the RoutePoint on that SingleRoute
     * @param point a pointCh
     * @return the RoutePoint that is the cloesest to the arg point.
     */

    @Override
    public RoutePoint pointClosestTo(PointCh point) {


        double position = checkPosition(edges.get(0).positionClosestTo(point),edges.get(0).length());

        PointCh projetedPointOnRoute = edges.get(0).pointAt(position);
        double distance = projetedPointOnRoute.distanceTo(point);

        int savedIndex = 0;
        for (int i = 1; i < edges.size() ; i++) {

            double otherPosition = checkPosition(edges.get(i).positionClosestTo(point),edges.get(i).length());
            PointCh otherPoint =  edges.get(i).pointAt(otherPosition);
            double otherDistance = otherPoint.distanceTo(point);

            if (otherDistance < distance){
                distance = otherDistance;
                position = otherPosition;
                projetedPointOnRoute = otherPoint;
                savedIndex=i;


            }

        }

        double totalLength = 0;

        for (int i = 0; i <= savedIndex-1 ; i++) {
            totalLength = totalLength + edges.get(i).length();


        }


          return  new RoutePoint(projetedPointOnRoute,position+totalLength,distance);


        }


    private double checkPosition(double position,double lengthEdge){
        return Math2.clamp(0,position,lengthEdge);
    }




}
