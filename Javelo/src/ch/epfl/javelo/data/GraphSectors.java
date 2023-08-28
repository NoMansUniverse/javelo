package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * record of GraphSector one arg this is a buffer wich contains the info firstNodeId and numberOfNode
 * @author Upeski Stefan (330129)
 */
public record GraphSectors(ByteBuffer buffer) {

    //Number of bytes that an int has

    private static final int OFFSET_FIRST_NODE = Integer.BYTES;
    //Number of bytes that an int has + Number of bytes that an short has === 6;

    private static final int OFFSET_NUMBER_NODE = OFFSET_FIRST_NODE + Short.BYTES;

  // lengthSquare to find a ratio

    private static final double SECTOR_WIDTH = SwissBounds.WIDTH / 128;
    private static final double SECTOR_HEIGHT = SwissBounds.HEIGHT / 128;




    /**
     * take two arg idNode and lengthNode and return endNodeId
     * @param firstNodeId id of the firstNode
     * @param nodeLength length of the corresponding node
     * @return int endNodeId
     */
    private static int convertNodeNumberToEndNodeId(int firstNodeId,short nodeLength){
        return  firstNodeId + Short.toUnsignedInt(nodeLength);

    }

    /**
     * record that create a Sector has two attributes startNodeId === id_firstNode extract from buffer
     */
    public record Sector(int startNodeId,int endNodeId){



    }



    private int lenghtCornerToPoint(double beginPoint,double SquareCordinate,boolean y){

        if(y){
            return (int) ( Math.floor(SquareCordinate-beginPoint)/SECTOR_HEIGHT);
        }

        return   (int) ( Math.floor(SquareCordinate-beginPoint)/SECTOR_WIDTH);

    }

    /**
     * take two args , one point wich is the center of a virtual square and distance is half of one edge of this "square"
     * @param center of the virtual square
     * @param distance useful to determine the area to search
     * @return ArrayList of Sectors that contain intersectedSector with the square
     */


    public  List<Sector> sectorsInArea(PointCh center, double distance){
        Preconditions.checkArgument(SwissBounds.containsEN(center.e(),center.n()));
        // Point(minE,minN) bottom left corner of the virtual grid of  xMax, xMin;
        double minN = SwissBounds.MIN_N;
        double minE = SwissBounds.MIN_E;


        // Point(minE,minN) bottom right corner of the virtual grid of Switzerland to find the yMin, yMax;

        double maxE = SwissBounds.MAX_E;
        double maxN = SwissBounds.MAX_N;

        ArrayList<Sector> intersectedSector = new ArrayList<Sector>();

        // compute the the differents square cordinates
        double SquareCordinatesx1 = Math2.clamp(minE,center.e()-distance,maxE);

        double SquareCordinatesx2 = Math2.clamp(minE,center.e()+distance,maxE);


        double SquareCordinatesy2 = Math2.clamp(minN,center.n()+distance,maxN);

        double SquareCordinatesy1 = Math2.clamp(minN,center.n()-distance,maxN);







        //check if sectorId is out of virtualSectorGrid, return the right idSector

        int xMin = Math2.clamp(0,lenghtCornerToPoint(minE,SquareCordinatesx1,false),127);
        int xMax = Math2.clamp(0,lenghtCornerToPoint(minE,SquareCordinatesx2,false),127);




        int yMin = Math2.clamp(0,lenghtCornerToPoint(minN,SquareCordinatesy1,true),127);
        int yMax = Math2.clamp(0, lenghtCornerToPoint(minN,SquareCordinatesy2,true),127);


        // two for loop to compute to find intersection of all with the virtual Square
        for (int i = xMin; i <= xMax  ; i++) {
            for (int j = yMin; j <= yMax ; j++) {



                int startNode =  buffer.getInt( ( OFFSET_NUMBER_NODE)  * ( j*128 + i )  );
                short lengthNode = buffer.getShort( OFFSET_NUMBER_NODE  * ( j*128 + i ) + OFFSET_FIRST_NODE );



                intersectedSector.add(new Sector(startNode,convertNodeNumberToEndNodeId(startNode,lengthNode)));
            }

        }



        return  intersectedSector;
    }
}
