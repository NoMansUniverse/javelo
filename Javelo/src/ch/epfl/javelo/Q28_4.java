package ch.epfl.javelo;
/**
 * @author Robin Bochatay(329724)
 */
public final class Q28_4 {
    private Q28_4(){}

    /**
     * return the Q28.4 value of the given integer
     * @param i the integer to be converted
     * @return int
     */
    public static int ofInt(int i){
        return i<<4;
    }

    /**
     * return the double value equal to the Q28.4 value given
     * @param q28_4 the double to be converted
     * @return double
     */
    public static double asDouble(int q28_4){
        return Math.scalb((double)q28_4,-4);
    }

    /**
     * return the float value equal to the Q28.4 value given
     * @param q28_4 the float to be converted
     * @return float
     */
    public static float asFloat(int q28_4){
        return Math.scalb(q28_4,-4);
    }
}
