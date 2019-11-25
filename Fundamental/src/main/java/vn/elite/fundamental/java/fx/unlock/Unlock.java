package vn.elite.fundamental.java.fx.unlock;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

/**
 * Main class for the Unlock Custom sample. This is boilerplate code: Loads 'Unlock.fxml', adds the root node to a
 * Scene, and set the scene to the application primary stage.
 * <br>In the Unlock Custom demo the key pad is defined as a custom type named Keypad.
 * From within Unlock.fxml we refer to the key pad by its Java class name, Keypad.
 */
public class Unlock extends Application {

    public static void main(String[] args) {
        launch(Unlock.class);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Pane page = FXMLLoader.load(Unlock.class.getResource("Unlock.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Unlock Custom Sample");
            primaryStage.show();
        } catch (Exception ex) {
            LoggerFactory.getLogger(Unlock.class).error("SEVERE", ex);
        }
    }
}
