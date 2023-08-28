package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;


/**
 * @author Upeski Stefan (330129)
 */

public record RoutePoint(PointCh point,double position,double distanceToReference) {
    public final   static RoutePoint NONE = new RoutePoint(null,Double.NaN,Double.POSITIVE_INFINITY);


    /**
     * Method that allow to shift a routePoint on a itiniery from a certain distance
     * @param positionDifference is a double that represent the positionDifference
     * @return the routePoint with a shiftedPosition
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){




        return new RoutePoint(this.point,this.position+positionDifference,this.distanceToReference);
    }

    /**
     * Method useful to check if a routePoint is close  from a point
     * @param that
     * @return a RoutePoint
     */
    public RoutePoint min(RoutePoint that){
        if(this.distanceToReference() <= that.distanceToReference){
            return this;
        }
        return that;
    }

    /**
     * Method useful to check if a routePoint is close  from a point
     * @param thatPoint a PointCh
     * @param thatPosition the position of thatPoint
     * @param thatDistanceToReference the distancetoReference
     * @return a RoutePoint
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        if(this.distanceToReference <= thatDistanceToReference){
            return  this;
        }
        return new RoutePoint(thatPoint,thatPosition,thatDistanceToReference);
    }

}
