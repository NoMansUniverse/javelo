package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import java.util.Arrays;
/**
 * @author Robin Bochatay(329724)
 */

public class ElevationProfileComputer {

    private ElevationProfileComputer(){};

    /**
     * compute the profile of the itinary, making sure the sample are spaced at max by maxStepLength
     * @param route route that we want to compute the profile from
     * @param maxStepLength max distance there can be between two sample
     * @throws if argument maxStepLength <0
     * @return the ElevationProfile of the given route
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength>0);

        //calcul du nbSample en fonction de la longueur de l'itinéraire et la longueur max des intevalles donné en argument
        int nbSample = (int)Math.ceil(route.length()/maxStepLength)+1;
        double inBetween = route.length()/(nbSample-1);
        float[] elevationProfile = new float[nbSample];

        //parcours toute les elevation de route et les stocks dans un tableau
        for(int i = 0; i<nbSample;i++){
          elevationProfile[i]= (float) route.elevationAt(inBetween*i);
        }

        //assert that tab is not full of 0
        if(isFullOfNaN(elevationProfile)){
            Arrays.fill(elevationProfile,0);
        }
        //Fill start of the tab with first legal value if it start with NaN,
        // fill end of tab with last valid value if last conatained value is NaN
        headTabFill(elevationProfile);
        queueTabFill(elevationProfile);

        for(int i=0; i<elevationProfile.length; i++){

            if(Float.isNaN(elevationProfile[i]))
            {
                float temp=0;

                //search for the first index with a valid elevation
                int k = firstIndexValid(elevationProfile,i);

                //go through all NaN elevation and compute its interpolated elevation between position between first valid point and last valid point
                for(int j=i; j<k; j++){
                    elevationProfile[j]=(float) Math2.interpolate(elevationProfile[i-1],elevationProfile[k] ,  (double)((j-i+1))/(k-i+1));
                }
                i=k;
            }
        }

        return new ElevationProfile(route.length(), elevationProfile);
    }

    /**
     * check if the tab is filled with NaN
     * @param tab float[] array
     * @return true if tab is full of NaN, false otherwise
     */
    private static boolean isFullOfNaN(float[] tab){
        float temp=0;
        int i =0;
        do{
            temp = tab[i];
            i++;
        }while(Float.isNaN(temp)&&i<tab.length);
        return (Float.isNaN(temp));
    }

    /**
     * fill the start of the tab with first legal value if the starting value are NaN
     * @param profile float[] array to be filled at the beginning
     * @return profile[] with first value that were NaN updated to first valid value
     */
    private static float[] headTabFill(float[] profile){
        float temp=0;
        int k=0;

        if(Float.isNaN(profile[0])) {
            //find the first value that is not NaN
            do {
                temp = profile[k];
                k++;
            }while (Float.isNaN(temp) && k < profile.length);

            Arrays.fill(profile, 0, k,temp);

            return profile;
        }
        else return profile;
    }

    /**
     * compute the first index valid(!=NaN) in array tab starting from a given index
     * @param tab array of float[] we want to find first index valid starting at indexStart
     * @param indexStart index at which we want to begin searching for a valid index
     * @return integer that is the valid index
     */
    private static int firstIndexValid(float[] tab,int indexStart){
        float temp=0;
        int k=indexStart;

        do{
            temp = tab[k];
            k++;
        }while(Float.isNaN(temp)&&k<tab.length);

        return k-1;
    }

    /**
     * fill the end of tab with last valid value if last values are NaN
     * @param profile float[] array that needs to be filled at the end
     * @return float[] the array profile[] but if last values were NaN, the previous NaN value are now replaced by last valid value
     */
    private static float[] queueTabFill(float[] profile){
        if(Float.isNaN(profile[profile.length-1])) {
            float temp = 0;
            int k = profile.length - 1;

            do {
                temp = profile[k];
                k--;
            } while(Float.isNaN(temp) && k >= 0);

            Arrays.fill(profile, k, profile.length, temp);

            return profile;
        }
        else return profile;
    }
}
