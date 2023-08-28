package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;
import java.util.StringJoiner;

/**
 * @author Robin Bochatay(329724)
 */

public record AttributeSet(long bits) {
    public AttributeSet{
        long OutOfBoundbits = bits>>>Attribute.COUNT;
        Preconditions.checkArgument(OutOfBoundbits==0);
    }

    /**
     * return an attribute set containing only attributes given as arguments
     * @param attributes
     * @return AttributeSet
     */
    public static AttributeSet of(Attribute... attributes){

        long bits=0L<<Attribute.COUNT;
        long mask;
        for(Attribute attribute : attributes){
            mask= 1L<< attribute.ordinal();
            bits=bits | mask;
        }
        return new AttributeSet(bits);
    }

    /**
     * check if the attribute is contained in the attributeSet
     * @param attribute
     * @return boolean
     */
    public boolean contains(Attribute attribute){

        long mask = 1L<< attribute.ordinal();

        if((mask&bits)!=0)
        {
            return true;
        }
        return false;
    }

    /**
     * check if the AttributeSet that is intersecting this AttributeSet
     * @param that
     * @return boolean
     */
    public boolean intersects(AttributeSet that){
        long intersection = that.bits & this.bits;
        if(intersection != 0)
        {
            return true;
        }
        return false;
    }
    public String toString(){
        StringJoiner j = new StringJoiner(",", "{","}");
        String finalString;
        for(Attribute attribute: Attribute.ALL){

            if(this.contains(attribute)){
                j.add(attribute.toString());
            }
        }
        return j.toString();
    }
}
