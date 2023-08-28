package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

public record PointCh(double e, double n) {
    public PointCh{
        Preconditions.checkArgument((SwissBounds.containsEN(e,n)));

    }

    /**
     * method that allow to compute the squared distance between this point and length
     * @param that is that
     * @return the square of the distance e ( of the Class) and  the paramas that in meters
     */
    public double squaredDistanceTo(PointCh that){
        return Math.pow(distanceTo(that),2);
    }

    /**
     * method that allow to compute the  distance between this point and length
     * @param that is that
     * @return the distance between e ( of the Class) and that in meters
     */
    public double distanceTo(PointCh that){
        double Ux = that.e()-this.e();
        double Uy = that.n()-this.n();
        return Math2.norm(Ux,Uy);
    }

    /**
     * method that return the longitude in radians
     * @return longitude in radians
     */
    public double lon(){
        return Ch1903.lon(e,n);
    }

    /**
     * methof that return the latitude in radians
     * @return  latitude in radians
     */
    public double lat(){
        return Ch1903.lat(e,n);
    }
}
