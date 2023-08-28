package ch.epfl.javelo;

/**
 * @author Robin Bochatay(329724)
 */

public final class Bits {
    private Bits() {}

    /**
     * extract a signed it out of vector of bit value, starting at bit start and ending at start+length
     * @param value vector of bits
     * @param start bits at which the selection start
     * @param length length of the wanted bits
     * @return signed integer of length length-start
     */
    public static int extractSigned(int value, int start, int length)
    {
        Preconditions.checkArgument((Integer.SIZE-length-start)>=0&&start>=0&&length>=0);

        int unsignedInt = (value << (Integer.SIZE-length-start))>> (Integer.SIZE-length);

        return unsignedInt;
    }
    /**
     * extract an  unsigned it out of vector of bit value, starting at bit start and ending at start+length
     * @param value vector of bits
     * @param start bits at which the selection start
     * @param length length of the wanted bits
     * @return unsigned integer extracted from value from start to length
     */
    public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument((Integer.SIZE-length-start)>=0&&start>=0&&length>=0);
        Preconditions.checkArgument(length!=32);

        return (value << (Integer.SIZE-length-start))>>> (Integer.SIZE-length);
    }


}
