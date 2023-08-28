package ch.epfl.javelo.projection;

public final class Ch1903 {
    private Ch1903(){}

    /**
     *  A function that take two args in radians, the latitude and the longitude that convert WGS84 to Swiss coordinates and return the east cordinates
     * @param lon in radians
     * @param lat in radians
     * @return East cordinates in meters
     */
    public static double e(double lon, double lat){
        double lon1 =Math.pow(10,-4)*(3600* Math.toDegrees(lon)-26782.5);
        double lat1 = Math.pow(10,-4)*(3600*Math.toDegrees(lat)-169028.66);
        double est = 2600072.37 + 211455.93*lon1-10938.51*lon1*lat1-0.36*lon1*Math.pow(lat1,2)-44.54*Math.pow(lon1,3);
        return est;
    }

    /**
     * A function that take two args in radians, the latitude and the longitude that convert WGS84 to Swiss coordinates and return the north cordinates
     * @param lon in radians
     * @param lat in radians
     * @return North cordinates in meters
     */


    public static double n(double lon, double lat){
        double lon1 =Math.pow(10,-4)*(3600* Math.toDegrees(lon)-26782.5);
        double lat1 = Math.pow(10,-4)*(3600*Math.toDegrees(lat)-169028.66);
        double north = 1200147.07+308807.95*lat1+3745.25*Math.pow(lon1,2)+76.63*Math.pow(lat1,2)-194.56*Math.pow(lon1,2)*lat1+119.79*Math.pow(lat1,3);
        return north;
    }

    /**
     * A function that take two args in meters, the east coordinates and the north coordinates, convert Swiss to  WGS 84 coordinates.
     * @param e in meters
     * @param n in meters
     * @return longitude in radians
     */
    public static double lon(double e, double n){
        double x = 0.000001*(e-2600000);
        double y = 0.000001*(n-1200000);

        double lon = 2.6779094+4.728982*x+0.791484*x*y+0.1306*x*y*y-0.0436*x*x*x;
        lon=lon*((double) 100/36);
        return Math.toRadians(lon);
    }

    /**
     *  A function that take two args in meters, the east coordinates and the north coordinates, convert Swiss to  WGS 84 coordinates.
     * @param e
     * @param n
     * @return latitude in radians
     */
    public static double lat(double e, double n){
        double x = 0.000001*(e-2600000);
        double y = 0.000001*(n-1200000);

        double lat = 16.9023892+3.238272*y-0.270978*x*x-0.002528*y*y-0.0447*x*x*y-0.0140*y*y*y;
        double division  = (100/36);
        return Math.toRadians(lat*(double) 100/36);
    }

}
