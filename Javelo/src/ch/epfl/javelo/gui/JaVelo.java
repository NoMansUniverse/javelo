
package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

import javafx.stage.Stage;

import java.nio.file.Path;


/**
 * @author Upeski Stefan (330129)
 */

public class JaVelo extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        ObjectProperty<MapViewParameters> mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);


        var cf = new CityBikeCF(graph);


        RouteComputer routeComputer = new RouteComputer(graph, cf);

        RouteBean routeBean = new RouteBean(routeComputer);





        /// MANAGER ////
        ErrorManager errorManager = new ErrorManager();

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph,tileManager,routeBean,errorManager::displayError);


        /// Create the main Pane for JaVelo ///
        BorderPane borderPane = new BorderPane();
        SplitPane splitPane = new SplitPane();
        splitPane.orientationProperty().set(Orientation.VERTICAL);




        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu();
        menu.setText("Fichier");





        MenuItem menuItem = new MenuItem();
        menuItem.setText("Exporter GPX");

        menuItem.disableProperty().bind(Bindings.createBooleanBinding(() -> routeBean.getRoute().getValue()==null,routeBean.getRoute()));
        menu.getItems().add(menuItem);

        menuBar.getMenus().add(menu);


        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(routeBean.getElevationProfile(), routeBean.highlightedPositionProperty());


        annotatedMapManager.pane().getChildren().add(errorManager.pane());

        splitPane.getItems().add(annotatedMapManager.pane());



        menuItem.setOnAction((e)->{
            GpxGenerator.writeGpx("javelo.gpx",routeBean.getRoute().getValue(),routeBean.getElevationProfile().getValue());
        });



        /// MANAGE what is display or not ///
        routeBean.getElevationProfile().addListener((p,o,n)->{

            if(splitPane.getItems().size() ==2){
                splitPane.getItems().remove(1);


            }
            if(n != null){
                splitPane.getItems().add(elevationProfileManager.pane());
            }

        });

        routeBean.highlightedPositionProperty().bind(Bindings.createDoubleBinding(()->{

            if(!Double.isNaN(annotatedMapManager.mousePositionOnRouteProperty().getValue())){
                return annotatedMapManager.mousePositionOnRouteProperty().getValue();
            }
            else{
                return elevationProfileManager.mousePositionOnProfileProperty().getValue();
            }
        }, elevationProfileManager.mousePositionOnProfileProperty(), annotatedMapManager.mousePositionOnRouteProperty()));

        SplitPane.setResizableWithParent(splitPane,false);
        borderPane.setTop(menuBar);


        borderPane.setCenter(splitPane);


        borderPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.show();
    }
}