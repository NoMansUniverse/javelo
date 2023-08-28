package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.awt.*;

/**
 * ErrorManager is the manager of all error that can occur, it handle the message error and display it,
 * @author Upeski Stefan (330129)
 */
public final class ErrorManager {
    private final VBox vBox = new VBox();
    private final SequentialTransition sequentialTransition;
    private  final  Text textToDisplay;


    public ErrorManager(){
        vBox.getStylesheets().add("error.css");
        vBox.setMouseTransparent(true);


        FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(200),vBox);
        fadeTransition1.setFromValue(0);

        fadeTransition1.setToValue(0.8);
        /// Pause transition for animation
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(2000));

        /// Second transition
        FadeTransition fadeTransition2 = new FadeTransition(Duration.millis(500),vBox);
        fadeTransition2.setFromValue(0.8);
        fadeTransition2.setToValue(0);




        textToDisplay = new Text();
        vBox.getChildren().add(textToDisplay);

        sequentialTransition = new SequentialTransition(fadeTransition1,pauseTransition,fadeTransition2);

    }


    /**
     * Method that return the current pane
     * @return a Pane that represent the current vBox with the error message
     */
    public Pane pane(){
        return vBox;
    }


    /**
     * Method that run and display the error message into the ui.
     * @param textError is a String that represent the error message to display.
     */
    public void displayError(String textError){
        textToDisplay.setText(textError);


        sequentialTransition.stop();


        Toolkit.getDefaultToolkit().beep();

        sequentialTransition.play();


    }
}
