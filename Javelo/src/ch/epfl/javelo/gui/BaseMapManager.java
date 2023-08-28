package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.IOException;

/**
 * BaseMap manager represent the manager of the main interaction between the user and the map
 * @author Upeski Stefan (330129)
 */
public final class BaseMapManager {

    private final  int SIZE_PIXEL = 256;

    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersObjectProperty;
    private final Pane pane;
    private  boolean redrawNeeded;
    private final Canvas canvas = new Canvas();
    private  WaypointsManager waypointManager;




    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> mapViewParametersObjectProperty) throws IOException {

        this.tileManager = tileManager;
        this.mapViewParametersObjectProperty = mapViewParametersObjectProperty;
        this.waypointManager= waypointsManager;
        this.pane = new Pane(canvas);

        //** CANVAS **//
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());






        handleClickMouse();
        listners();



    }


    private double getPaneWidth(){
        return pane().widthProperty().doubleValue();

    }

    private double getPaneHeight(){
        return pane().heightProperty().doubleValue();
    }

    private int getZoom(){
        return  (mapViewParametersObjectProperty.get().zoom());
    }


    /**
     * Method that return the current pane
     * @return a Pane that represent the current Pane with the tiles
     * @throws IOException if there is an error the stream of the tiles
     */
    public Pane pane(){
        return pane;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;


        try {
            computeSearchArea();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void listners(){
        /// PANE LISTNNERS
        pane.widthProperty().addListener((observable, oldValue, newValue) -> {
            pane.setPrefWidth(newValue.doubleValue());
            redrawOnNextPulse();
        });

        pane.heightProperty().addListener((observable, oldValue, newValue) -> {
            pane.setPrefWidth(newValue.doubleValue());
            redrawOnNextPulse();
        });


        mapViewParametersObjectProperty.addListener((observable, oldValue, newValue) -> {
            redrawOnNextPulse();
        });

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
    }

    private void handleClickMouse() {

          SimpleLongProperty minScrollTime = new SimpleLongProperty();
          ObjectProperty<Integer> zoomScroll = new SimpleObjectProperty<>();



        try {
            pane.setOnMouseClicked(e->{
                if(e.isStillSincePress()) {
                    waypointManager.addWayPoints(e.getX(), e.getY());
                }
            });
            ObjectProperty<Point2D> p1 = new SimpleObjectProperty<>(new Point2D(0,0));


            pane.setOnMousePressed(e->{
                p1.set(new Point2D(e.getX(),e.getY()));
            });
            pane.setOnMouseDragged(e->{
                Point2D p2=p1.get().subtract(new Point2D(e.getX(),e.getY()));
                Point2D topLeft = (mapViewParametersObjectProperty.get().topLeft()).add(p2);

                mapViewParametersObjectProperty.set(mapViewParametersObjectProperty.get().withMinXY(topLeft.getX(), topLeft.getY()));
                p1.set(new Point2D(e.getX(),e.getY()));
                p2=new Point2D(0,0);
            });
            pane.setOnMouseReleased(e->{
                        p1.setValue(null);
                    }
            );
            pane.setOnScroll(e -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime < minScrollTime.get()) return;
                minScrollTime.set(currentTime + 250);
                int zoomDelta = (int) Math.signum(e.getDeltaY());

                // clamp zoom between 8 and 19
               zoomDelta =  Math2.clamp(8,zoomDelta  + getZoom(),19);

               PointWebMercator zoomLeftPoint =  PointWebMercator.of(getZoom(),
                       mapViewParametersObjectProperty.get().leftCornerX(),
                       mapViewParametersObjectProperty.get().leftCornerY());

               PointWebMercator mousePositionBis = mapViewParametersObjectProperty.get().pointAt(e.getX(),e.getY());


               // create a new MapViewParameters
               MapViewParameters zoomMap = new MapViewParameters(
                        zoomDelta,
                        zoomLeftPoint.xAtZoomLevel(zoomDelta),
                        zoomLeftPoint.yAtZoomLevel(zoomDelta)
                );

                double x = mousePositionBis.xAtZoomLevel(zoomDelta)
                        -
                        zoomMap.pointAt(e.getX(),e.getY()).xAtZoomLevel(zoomDelta);
                double y = mousePositionBis.yAtZoomLevel(zoomDelta)
                        -
                        zoomMap.pointAt(e.getX(),e.getY()).yAtZoomLevel(zoomDelta);

                mapViewParametersObjectProperty.set(

                                zoomMap.withMinXY(
                                        zoomMap.leftCornerX()+x,
                                        zoomMap.leftCornerY()+y
                                )
                );

            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    private  void computeSearchArea() throws IOException {

        /// It's a variante of the idea of GraphSector to find the node
        double leftTopCornerX = mapViewParametersObjectProperty.get().leftCornerX();
        double leftTopCornerY = mapViewParametersObjectProperty.get().leftCornerY();


        double rightTopCornerX =  leftTopCornerX + getPaneWidth();
        double rightBottomCornerY = leftTopCornerY + getPaneHeight();

        int beginXIndex = (int) Math.floor(leftTopCornerX / 256d);
        int endXIndex = (int) Math.floor(rightTopCornerX / 256d);

        int beginYIndex = (int) Math.floor(leftTopCornerY / 256d);
        int endYIndex = (int) Math.floor(rightBottomCornerY / 256d);


        for (int i = beginXIndex; i <= endXIndex ; i++) {
            for (int j= beginYIndex; j <= endYIndex ; j++) {

                canvas.getGraphicsContext2D().drawImage(
                        tileManager.imageForTileAt(new TileManager.TileId(getZoom(), i, j)),
                        (SIZE_PIXEL*i-leftTopCornerX) ,(SIZE_PIXEL*j-leftTopCornerY));
            }
        }


    }

}
