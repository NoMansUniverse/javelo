package ch.epfl.javelo;
/**
 * @author Robin Bochatay(329724)
 * @author Stefan Upeski (330129)
 */
public final class Math2 {

    private Math2(){}

    /**
     * Gives the integer part (upper) of the division of x by y
     * @param x int numerator
     * @param y int denominator
     * @return int ceiled division of x divided by y
     */
    public static int ceilDiv(int x, int y){
        if(x <0 || y<=0) {
            Preconditions.checkArgument(false);
        }
        return (x + y-1) / y;
    }

    /**
     * return the coordinate y of the point found on the straight line going from (0,y0) and (1,y1) and coordinates x given
     * @param y0 ordinate at position 0
     * @param y1 ordinate at position 1
     * @param x position we want the ordinate of
     * @return double ordinate of position x
     */
    public static double interpolate(double y0,double y1,double x){

        return Math.fma(y1-y0,x,y0);
    }

    /**
     * limits the value of v to the interval going from min to max
     * return min if v is lower than min , max if v is larger than max and v otherwise
     *
     * @throws IllegalArgumentException if min >max
     * @param min min of the interval
     * @param v value to be clamped
     * @param max maximal value of interval
     * @return int return the value between min and max
     *         max if value is bigger than max
     *         min if value is smaller than min
     */
    public static int clamp(int min,int v,int max){
        if(min > max)
        {
            Preconditions.checkArgument(false);
        }
        if(v<min)
            return min;
        else if(v>max)
            return max;
        else
            return v;
    }

    /**
     *Check if the value v is in the intevalle [min,max] and return min if lower, max if larger
     * @param min min of the interval
     * @param v value to be clamped
     * @param max maximal value of interval
     * @return double return the value between min and max
     *         max if value is bigger than max
     *         min if value is smaller than min
     */
    public static double clamp(double min,double v, double max){

        Preconditions.checkArgument(min<=max);

        if(v<min) {
            return min;
        }
        else if(v>max) {
            return max;
        }
        return v;
    }

    /**
     * return the hyperbolic sinus inverse of its argument x
     * @param x value x
     * @return double asinh(x)
     */
    public static double asinh(double x){
        double lnX= x+Math.sqrt(1+Math.pow(x,2));

        return Math.log(lnX);
    }

    /**
     * return the dot product of vector (Ux,Uy) (vX,vY)
     * @param uX x-coordinate of vector u
     * @param uY y-coordinate of vector u
     * @param vX x-coordinate of vector u
     * @param vY y-coordinate of vector u
     * @return double dot product of u*v
     */
    public static double  dotProduct(double uX, double uY, double vX, double vY){
         return uX*vX+uY*vY;
    }

    /**
     * return the squared Norm of the vector (uX,uY)
     * @param uX x-coordinate of u
     * @param uY y-coordinate of u
     * @return double squared norm of vetor u
     */
    public static double squaredNorm(double uX, double uY){
        return Math.pow(uX,2)+Math.pow(uY,2);
    }

    /**
     * return the norm of the vector (uX,uY)
     * @param uX x-coordinate of u
     * @param uY y-coordinate of u
     * @return double norm of vector u
     */
    public static double norm(double uX, double uY){

        return Math.sqrt(squaredNorm(uX,uY));
    }

    /**
     * return the projection length in meters on the straight line defined by vector going from A to B
     * @param aX x-coordinate of vector a
     * @param aY y-coordinate of vector a
     * @param bX x-coordinate of vector b
     * @param bY y-coordinate of vector b
     * @param pX x-coordinate of vector p
     * @param pY y-coordinate of vector p
     * @return double
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        double apX = pX-aX;
        double apY = pY-aY;
        double abX= bX-aX;
        double abY = bY-aY;
        double l = dotProduct(apX,apY,abX,abY)/norm(abX,abY);
        return l;
    }

}
