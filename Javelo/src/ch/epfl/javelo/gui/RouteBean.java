package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import java.util.HashMap;

import java.util.*;

/**
 * @author Upeski Stefan (330129)
 */
public final class RouteBean {

    private final int CACHE_SIZE = 13;

    // OBSERVERVABLE OBJECT NEEDED //
    private final ObservableList<Waypoint> allWayPoints = FXCollections.observableArrayList();
    private final ObjectProperty<Route> route = new SimpleObjectProperty<>();
    private final DoubleProperty highLightedPosition = new SimpleDoubleProperty();
    private final ObjectProperty<ElevationProfile> elevationProfile = new SimpleObjectProperty<>();

    //CACHE//
    private final HashMap<Pair<Integer,Integer>, Route> memoryCache= new HashMap<>(CACHE_SIZE);



    public RouteBean(RouteComputer routeComputer){

        allWayPoints.addListener((Observable o) -> {

            if (checkIfWayPointsSizeList()){

                Route newRouteCreation = computeMultiRoute(allWayPoints,routeComputer);


                if(newRouteCreation !=null){
                    route.set(newRouteCreation);
                    elevationProfile.set(ElevationProfileComputer.elevationProfile(newRouteCreation,5));
                }
            }

            else{
                route.set(null);
                elevationProfile.set(null);
            }
        });


    }

    /**
     * Getter method that is the position on the Route but it's a DoubleProperty
     * @return DoubleProperty  that reprensent the current position
     */

    public DoubleProperty highlightedPositionProperty(){
        return this.highLightedPosition;
    }

    /**
     * Getter method for the position on the route
     * @return a double that reprensent the current position
     */

    public double highlightedPosition(){
       return highLightedPosition.get();
    }

    /**
     * Setter method for the position on the route
     * @param  newPosition  that is a double and  set the newPosition on the route
     */

    public void setHighlightedPosition(double newPosition){

        highLightedPosition.set(newPosition);
    }



    public ObservableList<Waypoint> getAllWayPoints() {
        return this.allWayPoints;
    }


    /**
     * Getter for the route
     * @return a ReandOnlyProperty of a Route that that reprensent all the itinary
     */
    public ReadOnlyProperty<Route> getRoute(){
        return this.route;
    }

    /**
     * Getter for the elevationProfile
     * @return a ReadOnlyProperty  of a elevationProfile that that reprensent all the itinary
     */
    public ReadOnlyProperty<ElevationProfile> getElevationProfile(){


        return this.elevationProfile;
    }
    /// check if a route exist in the cache  ///

    private boolean checkIfRouteExistCache(int startNodeId, int endNodeId){

        return memoryCache.containsKey(new Pair<>(startNodeId, endNodeId));
    }




    private Route computeMultiRoute(ObservableList<Waypoint> waypoints,RouteComputer routeComputer){
        ArrayList<Route> newRoutes = new ArrayList<>();

        HashMap<Pair<Integer,Integer>, Route>  memoryCacheBis = new HashMap<>(CACHE_SIZE);

        for (int i = 1; i < waypoints.size(); i++) {
            int wayPoint1Id = waypoints.get(i-1).closestNodeId();
            int wayPoint2Id = waypoints.get(i).closestNodeId();
            Pair<Integer, Integer> doubleKey = new Pair<>(wayPoint1Id,wayPoint2Id);

            if(checkIfRouteExistCache(wayPoint1Id,wayPoint2Id)){
                newRoutes.add(memoryCache.get(doubleKey));


                if (checkIfCacheBisIsFull(memoryCacheBis)){
                    memoryCacheBis.put(doubleKey,memoryCache.get(doubleKey));
                }
            }

            else {

                /// check if a itenary existe between two waypoints ///
                if(checkIfItenaryExist(routeComputer.bestRouteBetween(wayPoint1Id,wayPoint2Id))){

                    if(checkIfCacheBisIsFull(memoryCacheBis)){
                        memoryCacheBis.put(doubleKey,routeComputer.bestRouteBetween(wayPoint1Id,wayPoint2Id));
                    }
                    newRoutes.add(routeComputer.bestRouteBetween(wayPoint1Id,wayPoint2Id));
                }
                /// otherwise logically there is no route and no elevation ///

                else{
                    elevationProfile.set(null);
                    route.set(null);
                    return null;
                }
            }
        }
        /// clear cache ///
        memoryCache.clear();
        /// put cacheBis into cache attribute Class ///

        memoryCache.putAll(memoryCacheBis);

        return new MultiRoute(newRoutes);

    }
    /// check if a cache is full ///

    private boolean checkIfCacheBisIsFull(HashMap cache){

        return cache.size() != CACHE_SIZE;

    };
    /// check if the waypoint list contains at list 2 points ///


    private boolean checkIfWayPointsSizeList(){
        return allWayPoints.size() >= 2;
    }

    /// check if a itenary existe between two waypoints ///
    private boolean checkIfItenaryExist(Route route){

        return route != null;
    }

    public int indexOfNonEmptySegmentAt(double position) {
        int index = getRoute().getValue().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = getAllWayPoints().get(i).closestNodeId();
            int n2 = getAllWayPoints().get(i + 1).closestNodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }




}
