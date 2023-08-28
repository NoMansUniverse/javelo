package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;

import javafx.beans.Observable;
import javafx.geometry.Point2D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/**
 * @author Robin Bochatay (329724)
 */
public final class WaypointsManager {
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> param;
    private final List<Group> wayPointsGroup = new ArrayList<>();
    private final ObservableList<Waypoint> allWayPoint;
    private final Consumer<String> consumer;
    private final Pane pane;
    private final String EXT = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final String INT = "M0-23A1 1 0 000-29 1 1 0 000-23";


    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> param,
                            ObservableList<Waypoint> allWayPoints, Consumer<String> consumer) {
        this.graph = graph;
        this.param = param;
        this.consumer = consumer;
        this.allWayPoint = allWayPoints;
        this.pane = new Pane();

        pane.setPickOnBounds(false);
        listener();
        groupWayPoints(allWayPoints);
    }
    private void listener(){
            param.addListener((o, oV, nV) -> {
                if(allWayPoint.size()!=0) {
                    for (int i = 0; i < allWayPoint.size(); i++) {
                        Group g = wayPointsGroup.get(i);
                        PointCh point=allWayPoint.get(i).via();

                        locateWaypoint(g, param.get().viewX(PointWebMercator.ofPointCh(point)),
                                param.get().viewY(PointWebMercator.ofPointCh(point)));
                    }
                }
            });

            allWayPoint.addListener((Observable o)-> groupWayPoints(allWayPoint));


    }

    public Pane pane() {
        return pane;
    }

    public void addWayPoints(double x, double y) {
        PointCh point = (param.get()).pointAt(x, y).toPointCh();
        int nodeId = graph.nodeClosestTo(point, 500);


        if (nodeId != -1) {
            allWayPoint.add(new Waypoint(point, nodeId));
        } else {
            consumer.accept("No node within range");
        }
    }

    private void removeWayPoint(int index) {
        allWayPoint.remove(index);
    }

    private void locateWaypoint(Group g, double x, double y) {
        PointCh point = param.get().pointAt(x, y).toPointCh();
        int nodeClosestTo = graph.nodeClosestTo(point, 500);

        if (nodeClosestTo != -1) {
            g.setLayoutX(x);
            g.setLayoutY(y);
        } else {
            consumer.accept("no node within range");
        }
    }


    /**
     * method that given a list of waypoint attach to them their SVG path, set their position on the window, and manage the event
     * when a waypoint is dragged (move location), clicked on (removes it)
     * @param wayPoints
     */
    private void groupWayPoints(List<Waypoint> wayPoints) {
        wayPointsGroup.clear();
        for (int i = 0; i < wayPoints.size(); i++) {

            SVGPath EXT_LINE = new SVGPath();
            SVGPath INT_LINE = new SVGPath();

            EXT_LINE.setContent(EXT);
            INT_LINE.setContent(INT);

            EXT_LINE.getStyleClass().add("pin_outside");
            INT_LINE.getStyleClass().add("pin_inside");

            Group group = new Group(EXT_LINE, INT_LINE);

            if (!wayPointsGroup.contains(group)) {
                wayPointsGroup.add(group);

                PointCh point = wayPoints.get(i).via();
                locateWaypoint(group, param.get().viewX(PointWebMercator.ofPointCh(point)),
                        param.get().viewY(PointWebMercator.ofPointCh(point)));
                group.getStyleClass().add("pin");
            }
            eventHandler(group, i);
        }

        attachStyle();

        pane.getChildren().setAll(wayPointsGroup);
    }
    private void attachStyle(){
        if(!wayPointsGroup.isEmpty()) {
            wayPointsGroup.get(0).getStyleClass().add("first");

            if (wayPointsGroup.size() > 1) {
                wayPointsGroup.get(wayPointsGroup.size() - 1).getStyleClass().add("last");
            }
            if(wayPointsGroup.size()>2) {
                for (int i = 1; i < wayPointsGroup.size() - 1; i++) {
                    wayPointsGroup.get(i).getStyleClass().add("middle");
                }
            }
        }
    }
    private void eventHandler(Group group, int index){

        ObjectProperty<Point2D> p1 = new SimpleObjectProperty<>();
        ObjectProperty<Point2D> p2 = new SimpleObjectProperty<>();

        p1.set(new Point2D(0,0));
        p2.set(new Point2D(0,0));

        //vecteur entre position du click et pointe waypoint
        double xCoord = p1.getValue().getX() - p2.getValue().getX();
        double yCoord = p1.getValue().getY() - p2.getValue().getY();

        group.setOnMouseClicked(e -> {
            if(e.isStillSincePress()){
                removeWayPoint(index);
            }});
        group.setOnMousePressed(e -> {

                p1.getValue().add(group.getLayoutX(), group.getLayoutY());
                p2.getValue().add(e.getSceneX(), e.getSceneY());
        });
        group.setOnMouseDragged(e -> {
            locateWaypoint(group, e.getSceneX() + xCoord, e.getSceneY() + yCoord);
        });
        group.setOnMouseReleased(e -> {
            PointCh point = param.get().pointAt(e.getSceneX(), e.getSceneY()).toPointCh();
            if(!e.isStillSincePress()&&graph.nodeClosestTo(point,500)!=-1) {
                locateWaypoint(group, e.getSceneX() + xCoord, e.getSceneY() + yCoord);
                allWayPoint.set(index, new Waypoint(point, graph.nodeClosestTo(point, 500)));
            }
            else {

                point = allWayPoint.get(index).via();
                locateWaypoint(group,param.getValue().viewX(PointWebMercator.ofPointCh(point)),param.getValue().viewY(PointWebMercator.ofPointCh(point)));

            }
            });
    }
}
