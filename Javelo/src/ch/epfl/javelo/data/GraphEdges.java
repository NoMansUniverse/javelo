package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @author Robin Bochatay(329724)
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    private static final int OFFSET_EDGE_DIRECTION_DEST = Integer.BYTES;
    private static final int OFFSET_EDGE_LENGTH=OFFSET_EDGE_DIRECTION_DEST+Short.BYTES;
    private static final int OFFSET_INCLINE = OFFSET_EDGE_LENGTH+Short.BYTES;
    private static final int OFFSET_ATTRIBUTE_OSM = OFFSET_INCLINE+Short.BYTES;
    private static final int PROFILE_TYPE0=0;
    private static final int PROFILE_TYPE1=1;
    private static final int PROFILE_TYPE2=2;
    private static final int PROFILE_TYPE3=3;
    private static final int HALF =2;
    private static final int Q4_4=8;
    private static final int Q0_4=4;


    /**
     * check if the edge is going in the same direction as OSM way from which it comes(la voie OSM)
     * @param edgeId edge identity to be checked
     * @return true if it is going reverse (edgeId is negative)
     */
    public boolean isInverted(int edgeId){
        int edgeWayType = edgesBuffer.getInt(edgeId*(OFFSET_ATTRIBUTE_OSM));
        if(edgeWayType <0){
            return true;
        }
        return false;
    }

    /**
     * return the length of the edge
     * @param edgeId edge identity we want to find length of
     * @return double (Q28_4 in meters)
     */
    public double length(int edgeId){
        int edgeLength=Short.toUnsignedInt(edgesBuffer.getShort(edgeId*OFFSET_ATTRIBUTE_OSM+Integer.BYTES));
        return Q28_4.asDouble(edgeLength);
    }

    /**
     * return the total elevation gained along the edge edgeId
     * @param edgeId edge identity we want to find elevation gain
     * @return double (Q12_4 in meters)
     */
    public double elevationGain(int edgeId){
        int elevationGain=Short.toUnsignedInt(edgesBuffer.getShort(edgeId*OFFSET_ATTRIBUTE_OSM+OFFSET_EDGE_LENGTH));

        return Q28_4.asDouble(elevationGain);//conversion en Q12_4
    }

    /**
     * return the node towards which the edge is going
     * @param edgeId edge identity
     * @return int (nodeId of arrival)
     */
    public int targetNodeId(int edgeId){

        int nodeId = edgesBuffer.getInt(edgeId*OFFSET_ATTRIBUTE_OSM);

        if(isInverted(edgeId)) {
            return ~nodeId;
        }

        return nodeId;
    }

    /**
     * check that the edge has data on the profile
     * @param edgeId edge identity to be checked if it has a profil
     * @return boolean (false has no profile info/true has a profile)
     */
    public boolean hasProfile(int edgeId){
        int profileType = profileIds.get(edgeId);

        profileType= Bits.extractUnsigned(profileType, 30,2);
        if(profileType!=0)
        {
            return true;
        }

        return false;
    }

    /**
     * return the type of compression of the data stocked in ShortBuffer elevation
     * @param edgeId edge identity to find profile type of
     * @return integers between 0 and 3: 0 no profile; 1 profile not compressed; profile compressed in Q4.4; 3 profile compressed in Q0.4
     */
    private int profileType(int edgeId){
        int dataProfile = profileIds.get(edgeId);//recupère info profile correspondant a edgeid dans profileids à l'index edgeid
        int type= Bits.extractUnsigned(dataProfile, 30,2);

        return type;
    }

    /**
     * return the index of the first sample of elevation for edge edgeId
     * @param edgeId edge identity to find first index sample of
     * @return integer that is the index of first sample for edge edgeId
     */
    private int sampleIndex(int edgeId){
        int dataProfile = profileIds.get(edgeId);//recupère info profile correspondant a edgeid dans profileids à l'index edgeid
        int sampleIndex=  Bits.extractUnsigned(dataProfile,0,29);

        return sampleIndex;
    }

    /**
     * return the set of altitude constituing the profile of edge edgeId
     * @param edgeId edge identity to find profile of
     * @return an array of float[] that are the known altitude at different point
     */
    public float[] profileSamples(int edgeId){
        int type = profileType(edgeId);
        int indexSample = sampleIndex(edgeId);
        int edgeLength = Short.toUnsignedInt(edgesBuffer.getShort(edgeId * OFFSET_ATTRIBUTE_OSM + Integer.BYTES));
        int nbSample = 1+Math2.ceilDiv(edgeLength, Q28_4.ofInt(2));

        float[] profile = new float[nbSample];
        float[] delta= new float[nbSample];


        float firstElevation= Q28_4.asFloat(Short.toUnsignedInt(elevations.get(indexSample)));

        profile[0]=firstElevation;

        if(!hasProfile(edgeId)){
            return new float[0];
        }

        switch(type)
        {
            case 1:
                for(int i = indexSample; i<indexSample+nbSample;i+=1){
                    profile[i-indexSample]= Q28_4.asFloat(Short.toUnsignedInt(elevations.get(i)));
                }
                if(isInverted(edgeId)){
                    profile = inverseTab(profile);
                }
                    return profile;

                case 2:{
                    for(int i = indexSample+1; i<=indexSample+nbSample/2; i+=1){
                        //loop that extract byte out of short
                        for(int j=0; j<2; j+=1) {
                            short toExtract = elevations.get(i);
                            //we extract the difference only
                            delta[2*(i-indexSample-1)  +j]=Q28_4.asFloat(Bits.extractSigned(toExtract,(1-j)*Q4_4, Q4_4));
                        }
                    }
                    for(int i=0; i<delta.length-1;i+=1){
                        //add up to get the elevation
                        profile[i+1]=profile[i]+delta[i];
                    }


                    if(isInverted(edgeId))
                    {
                        profile = inverseTab(profile);
                    }

                    return profile;}
                case 3: {
                    int i = 1;
                    int count = 1;
                    while (i < nbSample) {
                        //loop that extract nibbles
                        for (int j = 0; j < 4; j++) {
                            if (i < nbSample) {
                                float elevation = Q28_4.asFloat(Bits.extractSigned(elevations.get(count + indexSample),
                                        (3 - j) * Q0_4, Q0_4));
                                profile[i] = profile[i - 1] + elevation;
                                i++;
                            }
                        }
                        count++;
                    }
                    if (isInverted(edgeId)) {
                        profile = inverseTab(profile);
                    }

                    return profile;
                }
            }

        return profile;
    }

    /**
     * inverse the set given in param
     * @param tab the array that needs to be inversed
     * @return float[] tab inversed
     */
    private float[] inverseTab(float[] tab){
        for(int i = 0; i < tab.length / 2.0; i++)
        {
            float temp = tab[i];
            tab[i] = tab[tab.length - i - 1];
            tab[tab.length - i - 1] = temp;

        }
        return tab;
    }

    /**
     * return the identity of the attributeSet attached to the edge edgeId
     * @param edgeId edge identity of attributeSet identity to be found
     * @return integer that is the identity of the attributeSet attached to the edge
     */
    public int attributesIndex(int edgeId){
        short edgeAttribute = edgesBuffer.getShort(edgeId*OFFSET_ATTRIBUTE_OSM+OFFSET_INCLINE);
        return Short.toUnsignedInt(edgeAttribute);
    }

}
