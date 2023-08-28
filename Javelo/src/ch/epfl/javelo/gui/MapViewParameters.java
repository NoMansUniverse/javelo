package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * @author Robin Bochatay (329724)
 */
public record MapViewParameters(int zoom, double leftCornerX, double leftCornerY) {

    public Point2D topLeft(){
        return new Point2D((float) leftCornerX,(float) leftCornerY);
    }
    public MapViewParameters withMinXY(double topLeftX,double topLeftY){
        return new MapViewParameters(zoom(), topLeftX,topLeftY);
    }
    public PointWebMercator pointAt(double x, double y){
        x+= leftCornerX();
        y+=leftCornerY();
        return PointWebMercator.of(zoom, x,y);
    }
    public double viewX(PointWebMercator p){
        return p.xAtZoomLevel(zoom)-leftCornerX;
    }
    public double viewY(PointWebMercator p){
        return p.yAtZoomLevel(zoom) -leftCornerY;
    }

}
