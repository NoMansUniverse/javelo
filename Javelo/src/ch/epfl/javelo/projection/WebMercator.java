package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * @author Upeski Stefan (330129)
 */

public final class WebMercator {

    /**
     *  Function that take lon and convert it x WebMercator coordinates
     * @param lon longitude in Radians
     * @return a double that represents x in WebMercator from the couple coordinates (x,y)
     */

    public static double x(double lon){

        double x = (lon+Math.PI)*(1.0/(Math.PI*2));


        return x;
    }

    /**
     *  Function that take lon and convert it y WebMercator coordinates
     * @param lat latitute in Radians
     * @return a double that represents y in WebMercator from the couple coordinates (x,y)
     */

    public static double y(double lat){

        double y = (Math.PI - Math2.asinh( Math.tan(lat) ) )
                *(1.0/(Math.PI*2));


        return y;
    }

    /**
     * Function that take double x WebMercator coordinates and convert it into lon in Radians
     * @param x coordinate in Webmercator
     * @return double lon  that represent the longitude in Radians
     */

    public static double lon(double x){


        return  2 * Math.PI * x  - Math.PI;
    }

    /**
     * Function that take double y WebMercator coordinates and convert it into lat in Radians
     * @param y coordinate in Webmercator
     * @return double lat (phi) that represent the longitude in Radians
     */

    public static double lat(double y){

        double phi = Math.atan( Math.sinh(Math.PI - (2.0 *Math.PI*y)) );

        return  phi;
    }

}
