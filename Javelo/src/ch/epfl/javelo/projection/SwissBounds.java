package ch.epfl.javelo.projection;

public final class SwissBounds {

    public final static double  MIN_E =2485000;
    public final static double MAX_E = 2834000;
    public final static double MIN_N= 1075000;
    public final static double MAX_N = 1296000;
    public final static double WIDTH = MAX_E-MIN_E;
    public final static double HEIGHT = MAX_N-MIN_N;

    private SwissBounds(){}

    /**
     * Function that check if some cordinates are in the swiss area.
     * @param e e cordinates in the swiss system
     * @param n n cordinates in the swiss system
     * @return true if the cordinates are in the swiss area and false oterwise.
     */
    public static boolean containsEN(double e, double n){
        return MAX_E >= e && MIN_E <= e && MAX_N >= n && MIN_N <= n;
    }
}
