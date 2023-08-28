package ch.epfl.javelo.giu;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.gui.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public class Stage8 extends Application {
    public static void mai(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("ch_west"));
        Path cacheBasePath = Path.of("cache");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);
        ObservableList<Waypoint> waypoints =
                FXCollections.observableArrayList(
                        new Waypoint(new PointCh(2532697, 1152350), 159049),
                        new Waypoint(new PointCh(2538659, 1154350), 117669));
        ErrorManager errorManager = new ErrorManager();

        var cf = new CityBikeCF(graph);

        RouteComputer routeComputer = new RouteComputer(graph, cf);
        RouteBean routeBean = new RouteBean(routeComputer);
        routeBean.setHighlightedPosition(1000);

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        routeBean.getAllWayPoints(),
                        errorManager::displayError);
        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);


        RouteManager routeManager =
                new RouteManager(routeBean,mapViewParametersP, errorManager::displayError);

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph,tileManager,routeBean,errorManager::displayError);


        annotatedMapManager.pane().getStylesheets().add("map.css");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(annotatedMapManager.pane()));
        primaryStage.show();
    }


}