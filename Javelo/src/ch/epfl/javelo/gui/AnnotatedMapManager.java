package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.beans.binding.Bindings;
import java.io.IOException;
import java.util.function.Consumer;

public final class AnnotatedMapManager {
    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final RouteManager routeManager;
    private final ObjectProperty<MapViewParameters> param;
    private final ObservableList<Waypoint> allWaypoints;
    private final Consumer<String> consumer;
    private final ObjectProperty<Point2D> mousePosition;
    private final DoubleProperty mousePositionOnRouteProperty;
    private final StackPane mainPane;
    private final RouteBean routeBean;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean routeBean, Consumer<String> consumer) {
        //INITIALISATION
        this.param = new SimpleObjectProperty<>(new MapViewParameters(12, 543200, 370650));
        this.mousePosition = new SimpleObjectProperty<>();
        this.routeBean = routeBean;
        this.mousePositionOnRouteProperty = new SimpleDoubleProperty(Double.NaN);
        this.consumer = consumer;
        this.routeManager = new RouteManager(this.routeBean, param);
        this.allWaypoints = routeBean.getAllWayPoints();
        this.waypointsManager = new WaypointsManager(graph, param, allWaypoints, consumer);

        try {
            this.baseMapManager = new BaseMapManager(tileManager, waypointsManager, param);
        } catch (IOException e) {
            throw new Error(e);
        }
        this.mainPane = new StackPane(baseMapManager.pane(), waypointsManager.pane(), routeManager.pane());

        mainPane.getStylesheets().add("map.css");

        //HANDLER
        handler();
        //BINDING
        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(() -> positionOnRoute(),
                                                                                param, mousePosition,
                                                                                        routeBean.getRoute()));

    }
    /**
     * set the handler on the mouse
     */
    private void handler(){
        mainPane.setOnMouseMoved(e->mousePosition.set(new Point2D(e.getX(),e.getY())));
        mainPane.setOnMouseExited(e->positionOnRoute());
    }

    /*
     * calculate the position of the mouse on route if mouse position is at a distance less than 15pixel, else return NaN
     * @return
     */
    private double positionOnRoute() {

            if (routeBean.getRoute().getValue() != null && mousePosition.getValue() != null) {
                PointCh pointAt = param.getValue().pointAt(mousePosition.getValue().getX(), mousePosition.getValue().getY()).toPointCh();
                if(pointAt!= null) {
                    RoutePoint routePoint = routeBean.getRoute().getValue().pointClosestTo(pointAt);

                    PointWebMercator p1 = PointWebMercator.ofPointCh(pointAt);
                    PointWebMercator p2 = PointWebMercator.ofPointCh(routePoint.point());

                    double p1X = param.getValue().viewX(p1);
                    double p1Y = param.getValue().viewY(p1);
                    double p2X = param.getValue().viewX(p2);
                    double p2Y = param.getValue().viewY(p2);

                    if (Math2.norm(p2X - p1X, p2Y - p1Y) <= 15) {
                        return routePoint.position();

                    }

                }
            }
        return Double.NaN;
    }

    /**
     * return the pane containing map, route and bean
     * @return
     */
    public Pane pane(){
        return mainPane;
    }

    /**
     * return the mouse position on route in a property
     * @return DoubleProperty
     */
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty(){
        return mousePositionOnRouteProperty;
    }
}
