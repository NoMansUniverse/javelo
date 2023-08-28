package ch.epfl.javelo;

public final class Preconditions {
    private Preconditions(){}

    /**
     * check if the argument shouldBeTrue is true and if false throws an IllegalArgumentException
     * @throws IllegalArgumentException if shouldBeTrue is false
     * @param shouldBeTrue logic statement that shoulde be true
     */
    public static void  checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }

}
