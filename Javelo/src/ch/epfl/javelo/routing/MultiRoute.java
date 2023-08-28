package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Upeski Stefan (330129)
 */


public class MultiRoute implements Route{
    private final List<Route> routes;

    private double checkPosition(double position)
    {
      return   Math2.clamp(0,position,this.length());
    }


    /**
     * constructor of multiRoute
     * @param segments  is a List composed with Multiroute or SingleRoute
     * @throws IllegalArgumentException if the List of segments is empty
     */
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());
        this.routes = List.copyOf(segments);
    }


    /**
     * Function that return the index of a segment
     * @param position on all itinary
     * @return the index of the corresponding segment
     */
    @Override
    public int indexOfSegmentAt(double position) {
        position=checkPosition(position);
        int indexSegment = 0;
        double countLength = 0;

        for (Route route : routes) {

            if (route.length() + countLength < position) {
                countLength = countLength + route.length();
                indexSegment += route.indexOfSegmentAt(route.length())+1;
            }

            else {
                double difference = position - countLength;
                indexSegment += route.indexOfSegmentAt(difference);
                return indexSegment;
            }
        }
        return indexSegment;


    }

    /**
     * Function that return the total length of the itinary
     * @return the total length of the itinary
     */
    @Override
    public double length() {
        double countRoute = 0;

        for (Route route : routes) {
            countRoute = countRoute + route.length();
        }
        return countRoute;
    }


    /**
     * Function that return the total edges of the itinary
     * @return all the edges of the itinary
     */

    @Override
    public List<Edge> edges() {
        ArrayList<Edge> edges = new ArrayList<>();
        for (Route route : routes) {
            edges.addAll(route.edges());
        }
        return edges;
    }

    /**
     * List de PointCh qui contient l'emsemble des points se trouvant sur un itinéaire
     * @return a List that return distinguish Points that are on a SingleRoute
     */

    @Override
    public List<PointCh> points() {
        ArrayList<PointCh> points = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            if (i == routes.size()-1){
                points.addAll(routes.get(i).points());
            }
            else{
                points.addAll(routes.get(i).points());
                points.remove(points.size()-1);
            }

        }


        return points;
    }

    /**
     * Function take a double position , this is a position on the Single and give the point on the SingleRoute that is a that position
     * @param position the position on a intinary
     * @return a PointCh on Single that is a that position
     */

    @Override
    public PointCh pointAt(double position) {


        int index =0;
        position = checkPosition(position);
        double reducePosition = position;


        for (int i = 0; i <routes.size() ; i++) {

           if(reducePosition > routes.get(i).length()){
               reducePosition = reducePosition - routes.get(i).length();
           }
           else{
               index = i;

               break;
           }
        }

        return routes.get(index).pointAt(reducePosition);
    }


    /**
     * Function take a double position , this is a position on the Single and give the point on the SingleRoute that is a that position
     * @param position
     * @return the eleveation of that position
     */

    @Override
    public double elevationAt(double position) {
        int index =0;
        position = checkPosition(position);
        double reducePosition = position;




        for (int i = 0; i <routes.size() ; i++) {

            if(reducePosition > routes.get(i).length()){
                reducePosition = reducePosition - routes.get(i).length();
            }
            else{
                index = i;

                break;
            }
        }

        // on get l'index ensuite

        return routes.get(index).elevationAt(reducePosition);
    }

    /**
     * Function take a double position , this is a position on the Single and give the closest node on the SingleRoute that is a that position
     * @param position the position on a intinery
     * @return a int NodeId
     */


    @Override
    public int nodeClosestTo(double position) {
        int index =0;
        position = checkPosition(position);
        double reducePosition = position;

        for (int i = 0; i <routes.size() ; i++) {

            if(reducePosition > routes.get(i).length()){
                reducePosition = reducePosition - routes.get(i).length();
            }
            else{
                index = i;

                break;
            }


        }
        // on get l'index ensuite
        return routes.get(index).nodeClosestTo(reducePosition);
    }

    /**
     *  Function that take point on arg , and basically to geometrical stuff like projection to find the RoutePoint on that SingleRoute
     * @param point a pointCh
     * @return the RoutePoint that is the closest to the point.
     */

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint minRoute = RoutePoint.NONE;
        double totalLength = 0;


        for (int i = 0; i < routes.size(); i++) {

            minRoute = minRoute.min(routes.get(i).pointClosestTo(point).withPositionShiftedBy(totalLength));
            //get length
            totalLength = totalLength + routes.get(i).length();


        }


        return  minRoute;

    }



}
