package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * @author Upeski Stefan (330129)
 */

public record PointWebMercator(double x, double y) {

    /**
     * PointWebMercator constructor that check that the couple (x,y) are in the interval [0,1]
     * @param x is x coordinate
     * @param y is y coordinate
     * @throws IllegalArgumentException if x and y are not in the interval [0,1]
     */

    public PointWebMercator{
        Preconditions.checkArgument(x >= 0 && x <= 1 && y >= 0 && y <= 1);

    }

    /**
     * Function that take three args zoomlevel, and the coordinates (x,y)
     * and return the coordinates that are in the same level
     * @param zoomLevel int the zoomlevel
     * @param x is x coordinate
     * @param y is y coordinate
     * @return return the coordinates that are at the same level of zoomlevel
     */
    public static PointWebMercator of(int zoomLevel, double x , double y){



        PointWebMercator pointDezoomed = new PointWebMercator(Math.scalb(x,-zoomLevel-8),Math.scalb(y,-zoomLevel-8));


        return pointDezoomed;

    }

    /**
     * Function that take one arg Swiss Point (e,n) , it's allow to create WebMercator object using pointch
     * @param pointCh pointCh
     * @return PointWebMercator Point
     */

    public static  PointWebMercator ofPointCh(PointCh pointCh){



        PointWebMercator convertMercator = new PointWebMercator(WebMercator.x(pointCh.lon()),WebMercator.y(pointCh.lat()));

        return convertMercator;
    }

    /**
     * Function that take int zoomlvl and return the cordinates x Mercator at the same zoom lvl
     * @param zoomLevel is the int zoomlevel
     * @return x that is at the same zoomlevel.
     */


    public  double xAtZoomLevel(int zoomLevel){
        double xZoom = Math.scalb(x(),zoomLevel+8);


        return xZoom;

    }

    /**
     * Function that take int zoomlvl and return the cordinates y Mercator at the same zoom lvl
     * @param zoomLevel is the int zoomlevel
     * @return y that is at the same zoomlevel.
     */

    public  double yAtZoomLevel(int zoomLevel){
        double yZoom = Math.scalb(y(),zoomLevel+8);


        return yZoom;


    }

    /**
     * Function use WebMercator function lon that take x Webmercator
     * @return a lon in Radians
     */

    public double lon(){
       return WebMercator.lon(x());
    }

    /**
     * Function use WebMercator function lon that take y Webmercator
     * @return a lat in Radians
     */

    public double lat(){
        return WebMercator.lat(y());
    }

    /**
     * Function that convert WebMercator (x,y) to SwissPoint (e,n) (PointCh), and check if (e,n) are in the Swissbounds
     * @return a PointCh (e,n) or null if it's not SwissBound.
     */

    public PointCh toPointCh(){

        double e = Ch1903.e(lon(),lat());
        double n = Ch1903.n(lon(),lat());

        if(!SwissBounds.containsEN(e,n)){
            return null;
        }

        return new PointCh(e,n);
    }

}
