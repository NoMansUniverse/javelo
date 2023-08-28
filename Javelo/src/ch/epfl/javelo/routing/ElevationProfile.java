package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;
import java.util.function.DoubleUnaryOperator;

/**
 * @author Robin Bochatay (329724)
 */

public class ElevationProfile {

    private final double LENGTH;
    private final float[] elevationSamples;
    private final double TOTALASCENT;
    private final double TOTALDESCENT;

    /**
     * construct the profile
     * @throws IllegalArgumentException if length<0 | elevationSample contains less than 2 samples
     * @param length length of the profile
     * @param elevationSamples array of the sample of elevations
     */
    public ElevationProfile(double length, float[] elevationSamples){

        Preconditions.checkArgument(length>0&& elevationSamples.length>=2);

        LENGTH=length;
        this.elevationSamples=elevationSamples;
        double totalAscent=0;
        double totalDescent=0;

        for(int i=1; i<elevationSamples.length; i++)
        {
            double delta = elevationSamples[i]-elevationSamples[i-1];
            if(delta>0){
                totalAscent = totalAscent + delta;
            }
            else{
                totalDescent= totalDescent -delta;
            }
        }
        TOTALASCENT=totalAscent;
        TOTALDESCENT=totalDescent;

    }

    /**
     * return the length of the profile in meters
     * @return double length of profile in meters
     */
    public double length(){
        return LENGTH;
    }
    public double minElevation(){

        double min=elevationSamples[0];

        for(int i = 0; i< elevationSamples.length;i+=1){
            if(elevationSamples[i]<min){
                min=elevationSamples[i];
            }
        }
        return min;
    }

    /**
     * compute the max elevation of the profile
     * @return double max elevation of the profile
     */
    public double maxElevation(){

        double max=elevationSamples[0];

        for(int i = 0; i< elevationSamples.length;i+=1){
            if(elevationSamples[i]>max){
                max=elevationSamples[i];
            }
        }
        return max;
    }

    /**
     * compute the total positive elevation of the profile in meters
     * @return double total positive elevation in meter
     */
    public double totalAscent(){ return TOTALASCENT;}

    /**
     * compute the total negative elevation of the profile in meters(the value is positive)
     * @return double total descent in meter
     */
    public double totalDescent(){
        return TOTALDESCENT;
    }

    /**
     * compute the height of the point at position position
     * @param position position on x axis
     * @return double elevation at the given position
     */
    public double elevationAt(double position){

        DoubleUnaryOperator function= Functions.sampled(elevationSamples,LENGTH);
        return function.applyAsDouble(position);
    }
}
