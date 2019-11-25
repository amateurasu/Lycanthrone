package vn.elite.snatcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Snatcher extends Application {

    public AnchorPane tabDownload;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Pane page = FXMLLoader.load(Snatcher.class.getResource("Snatcher.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(400);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Snatcher");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
