package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

/**
 * @author Robin Bochatay (329724)
 */
public final class ElevationProfileManager {



    private final Pane pane;
    private final BorderPane borderPane;
    private final VBox vBox;
    private final Insets INSET;
    private final Line line;
    private final Path grid;
    private final Polygon profile;
    private final Group textGroup = new Group();

    private final ReadOnlyDoubleProperty position;
    private final ReadOnlyProperty<ElevationProfile> elevationProfile;
    private final DoubleProperty mousePositionOnProfileProperty;
    private final ObjectProperty<Rectangle2D> blueRectangle;
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Transform> worldToScreen;


    private final int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private final int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };




    public ElevationProfileManager(ReadOnlyProperty<ElevationProfile> elevationProfile, ReadOnlyDoubleProperty position){

        this.elevationProfile = elevationProfile;
        this.position = position;
        this.borderPane = new BorderPane();
        this.pane = new Pane();
        this.blueRectangle = new SimpleObjectProperty<>();
        this.vBox=new VBox();
        this.line = new Line();
        this.grid = new Path();
        this.profile= new Polygon();
        this.INSET = new Insets(10,10,20,40);
        this.mousePositionOnProfileProperty=new SimpleDoubleProperty(Double.NaN);
        this.screenToWorld=new SimpleObjectProperty<>(new Affine());
        this.worldToScreen =new SimpleObjectProperty<>(new Affine());

        pane.getChildren().add(profile);
        pane.getChildren().add(line);

        mouseHandler();

        //LISTENERS
        addListeners();
        elevationProfile.addListener((o,oV,nV)-> {
            if(nV!= null) {
                update();
            }});
        //M
        constructionPaneProfile();

    }

    public Pane pane(){
        return borderPane;
    }

    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        return mousePositionOnProfileProperty;
    }

    /**
     * Compute value of linear affine in respect to the size of the window and profile
     * @throws NonInvertibleTransformException
     */
    private void computeAffine() throws NonInvertibleTransformException {

        Affine affine = new Affine();
        double SCALING_X=elevationProfile.getValue().length()/blueRectangle.getValue().getWidth();
        double SCALING_Y=(elevationProfile.getValue().maxElevation()
                            -elevationProfile.getValue().minElevation())/blueRectangle.getValue().getHeight();

        affine.prependTranslation(-blueRectangle.get().getMinX(),-blueRectangle.get().getMinY());
        affine.prependScale(SCALING_X, -SCALING_Y);
        affine.prependTranslation(0,elevationProfile.getValue().maxElevation());

        screenToWorld.set(affine);
        worldToScreen.set(screenToWorld.get().createInverse());
    }

    /**
     * setup the listeners on the pane, rectangle framing the profile, and the profile itself
     */
    private void addListeners(){
       // pane.widthProperty().addListener(e ->update());
        //pane.heightProperty().addListener(e -> update());

        blueRectangle.bind(Bindings.createObjectBinding(()->  new Rectangle2D(INSET.getLeft(), INSET.getTop(),
                Math.max(pane.getWidth()  - INSET.getRight()-INSET.getLeft(),0),
                Math.max(pane.getHeight() - INSET.getTop()-INSET.getBottom(),0)),  pane.widthProperty(), pane.heightProperty()));
        blueRectangle.addListener((o, oV,nV)->update());
        elevationProfile.addListener((o, oV,nV)-> {
            if(elevationProfile.getValue()!=null) {
                profileCompute();
            }
        });
    }

    /**
     * calls all the private methods needing update
     */
    private void update(){
        //pane.getChildren().clear();
        vBox.getChildren().clear();
        if(elevationProfile.getValue()!=null) {
            try {
                computeAffine();
            } catch (NonInvertibleTransformException e) {
                throw new Error(e);
            }
            grid();
            profileCompute();
        }

        Text text = statistic();
        vBox.getChildren().add(text);
    }

    /**
     * compute the grid attapted to profile and window, and set the text for the profile
     */
    private void grid() {
        grid.getElements().clear();
        pane.getChildren().remove(grid);
        textGroup.getChildren().clear();
        double stepX = stepX();
        double stepY = stepY();




        if (stepX != 0 & stepY != 0) {
            //CREATING VERTICAL LINES
            for (int i = 0; stepX * i < blueRectangle.getValue().getWidth(); i++) {

                double x = blueRectangle.getValue().getMinX() + i * stepX;

                PathElement moveTo = new MoveTo(x, blueRectangle.getValue().getMaxY());
                PathElement lineTo = new LineTo(x, blueRectangle.getValue().getMinY());

                grid.getElements().addAll(moveTo,lineTo);

                int position = (int) Math.rint(screenToWorld.getValue().transform(x, 0).getX() / 1000);


                Text text = new Text(String.valueOf(position));
                javafx.scene.text.Font.font("Avenir", 10);
                text.setTextOrigin(VPos.CENTER);
                double spacing = text.prefHeight(0) / 2;
                text.setX(x);
                text.setY(blueRectangle.getValue().getMaxY() + spacing);
                text.getStyleClass().add("grid_label");
                text.getStyleClass().add("vertical");
                textGroup.getChildren().add(text);

            }
            //CREATING HORIZONTAL LINES
            for (int i = 0;  i*stepY<=elevationProfile.getValue().maxElevation(); i++) {
                double x = blueRectangle.getValue().getMinX();
                if(worldToScreen.getValue().transform(0,i*stepY).getY()<=blueRectangle.getValue().getMaxY()){
                    double y =worldToScreen.getValue().transform(0,i*stepY).getY();

                    PathElement moveTo = new MoveTo(x, y);
                    PathElement lineTo = new LineTo(blueRectangle.getValue().getMaxX(), y);


                    grid.getElements().addAll(moveTo,lineTo);


                    Text text = new Text(String.valueOf((int) Math.rint(i*stepY)));
                    javafx.scene.text.Font.font("Avenir", 10);
                    text.setTextOrigin(VPos.CENTER);
                    double spacing = text.prefWidth(0);

                    text.setX(x - spacing);
                    text.setY(y);
                    text.getStyleClass().add("grid_label");
                    text.getStyleClass().add("horizontal");

                    textGroup.getChildren().add(text);

                }
            }


            grid.setId("grid");
            pane.getChildren().add(grid);
        }
    }

    /**
     * calculate the spacing between lines for the x-axis
     * @return
     */
    private double stepX(){
        int step = 100_000;
        for(int i =0; i<POS_STEPS.length;i++){
            if(worldToScreen.getValue().deltaTransform(POS_STEPS[i],0).getX()>=50 && POS_STEPS[i]<step){
                step = POS_STEPS[i];
            }
        }

        return worldToScreen.getValue().deltaTransform(step,0).getX();
    }

    /**
     * compute the spacing between lines for the y-axis
     * @return
     */
    private double stepY(){
        int stepSize=1_000;
        for(int i=0; i< ELE_STEPS.length; i++){
            if(worldToScreen.getValue().deltaTransform(0,ELE_STEPS[i]).getY() <=-25 && ELE_STEPS[i]<stepSize){
                stepSize=ELE_STEPS[i];
            }
        }
        return stepSize;
    }
    /**
     * statistic text
     */
    private Text statistic(){
        if(elevationProfile.getValue()!=null) {
            Text t = new Text(String.format("Longueur : %.1f km" +
                            "     Montée : %.0f m" +
                            "     Descente : %.0f m" +
                            "     Altitude : de %.0f m à %.0f m", elevationProfile.getValue().length() / 1000,
                    elevationProfile.getValue().totalAscent(),
                    elevationProfile.getValue().totalDescent(),
                    elevationProfile.getValue().minElevation(),
                    elevationProfile.getValue().maxElevation()));
            return t;
        }
        return null;
    }

    /**
     * compute the polygon forming the profile, depending on the rectangle framing the profile and the window
     * @return polygon composing the profile
     */
    private void profileCompute(){

        profile.getPoints().clear();
        //PROFILE
        profile.getPoints().addAll(blueRectangle.getValue().getMinX(), blueRectangle.getValue().getMaxY());

            for (double i = blueRectangle.get().getMinX(); i < blueRectangle.getValue().getMaxX(); i++) {


                double position = screenToWorld.getValue().transform(i, 0).getX();
                double elevation = elevationProfile.getValue().elevationAt(position);

                profile.getPoints().add(i);
                profile.getPoints().add(worldToScreen.getValue().transform(position, elevation).getY());


            }

            //BOTTOM LEFT CORNER
            //BOTTOM RIGHT CORNER
            profile.getPoints().addAll(blueRectangle.getValue().getMaxX(), blueRectangle.getValue().getMaxY());

        //pane.getChildren().add(profile);
        profile.setId("profile");

    }

    /**
     * set up pane and vbox , add stylesheet and compute affine, draw line on profile
     */
    private void constructionPaneProfile(){

        borderPane.setBottom(vBox);
        borderPane.getStylesheets().add("elevation_profile.css");
        borderPane.setCenter(pane);
        pane.getChildren().add(textGroup);

        drawLine();


    }

    /**
     * bind properties in order to setup line on profile
     */
    private void drawLine(){
        line.layoutXProperty().bind(Bindings.createDoubleBinding(()->{
            return worldToScreen.getValue().transform(position.getValue(),0).getX();
            }, position, worldToScreen));
        line.startYProperty().bind(Bindings.select(blueRectangle, "MinY"));
        line.endYProperty().bind(Bindings.select(blueRectangle,"MaxY"));
        /*Bindings.createBooleanBinding(()->
                position.greaterThanOrEqualTo(0), mousePositionOnProfileProperty,position)*/
        line.visibleProperty().bind(position.greaterThanOrEqualTo(0));
    }

    /**
     * handle event in relation to mouse
     */
    private void mouseHandler(){
        pane.setOnMouseMoved(e-> {
            if(blueRectangle.get().contains(e.getX(), e.getY())){

                mousePositionOnProfileProperty.set(screenToWorld.getValue().transform(e.getX(),0).getX());
            }
            else {

                mousePositionOnProfileProperty.set(Double.NaN);
            }
        });
        pane.setOnMouseExited(e->{
            mousePositionOnProfileProperty.set(Double.NaN);
        });
    }
}