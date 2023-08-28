package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/**
 * @author Robin Bochatay (329724)
 */
public final class RouteManager {
    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> paramView;
    private final Pane pane;
    private final Polyline line;
    private final Circle circle;

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> paramView){

        this.routeBean=routeBean;
        this.paramView=paramView;
        this.pane=new Pane();
        this.line=new Polyline();
        this.circle = new Circle(5);

        line.setId("route");
        circle.setId("highlight");


        createPolyline();
        clickHandler();

       //LISTENERS
        listener();

        pane.setPickOnBounds(false);

    }
    private void listener(){
        ObjectProperty<Point2D> p1= new SimpleObjectProperty<>();
        p1.set(new Point2D(0,0));

        paramView.addListener((o,oV,nV)->{
            if(oV.zoom()!=nV.zoom()){
                p1.set(nV.topLeft());
                createPolyline();
                drawPolyline(0 , 0);
            }
            else
            {
                drawPolyline(p1.get().getX()-nV.leftCornerX(), p1.get().getY()-nV.leftCornerY());
            }

        });

        routeBean.highlightedPositionProperty().addListener((o,oV,nV)->{
            if(routeBean.getRoute()!=null&&!Double.isNaN(routeBean.highlightedPosition())) {

                locateCircle();
                circle.setVisible(true);
            }
            else circle.setVisible(false);
        });

        routeBean.getRoute().addListener((o,oV,nV)->{

            if(nV==null) {

                line.setVisible(false);
                circle.setVisible(false);
            }
            else{
                p1.set(paramView.get().topLeft());
                createPolyline();
                drawPolyline(0,0);
            }
        });
    }

    /**
     * compute the point forming the point where the line as to get by in point webMercator
     */
    private void createPolyline(){


        if(routeBean.getRoute().getValue()!=null) {
            pane.getChildren().clear();
            List<Double> pointList = new ArrayList<>();

            //get the pointch by which the route goes by, convert it to point webmercator
            for (PointCh pointCh: routeBean.getRoute().getValue().points()) {

                PointWebMercator point = PointWebMercator.ofPointCh(pointCh);

                pointList.add(paramView.get().viewX(point));
                pointList.add(paramView.get().viewY(point));

            }

            line.getPoints().setAll(pointList);
            pane.getChildren().add(line);
            pane.getChildren().add(circle);
            line.setVisible(true);
        }
        else{
            line.setVisible(false);
        }
    }

    private void drawPolyline(double x, double y){
        if(routeBean.getRoute().getValue()!=null){
            line.setLayoutX(x);
            line.setLayoutY(y);
            locateCircle();
        }
    }
    private void locateCircle(){
        if(this.routeBean.getRoute().getValue()!= null) {
            PointCh p = routeBean.getRoute().getValue().pointAt(routeBean.highlightedPosition());
            PointWebMercator pWM = PointWebMercator.ofPointCh(p);

            circle.setLayoutX(paramView.get().viewX(pWM));
            circle.setLayoutY(paramView.get().viewY(pWM));
        }
    }
    private void clickHandler(){
        circle.setOnMouseClicked(e->{
            Point2D position = circle.localToParent(e.getX(),e.getY());
            PointCh topnch = paramView.get().pointAt(position.getX(),position.getY()).toPointCh();
            int i = routeBean.indexOfNonEmptySegmentAt(routeBean.getRoute().getValue().pointClosestTo(topnch).
                    position());

            int nodeId = routeBean.getRoute().getValue().nodeClosestTo(routeBean.getRoute().getValue().pointClosestTo(topnch).
                    position());
            Waypoint waypoint = new Waypoint(topnch,nodeId);
            routeBean.getAllWayPoints().add(i+1, waypoint);

        });
    }
    public Pane pane(){
        return pane;
    }

}
