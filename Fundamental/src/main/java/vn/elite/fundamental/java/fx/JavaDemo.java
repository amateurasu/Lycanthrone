package vn.elite.fundamental.java.fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //remove window decoration
        primaryStage.initStyle(StageStyle.UNDECORATED);

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: green;");

        ToolBar toolBar = new ToolBar();

        int height = 25;
        toolBar.setPrefHeight(height);
        toolBar.setMinHeight(height);
        toolBar.setMaxHeight(height);
        toolBar.getItems().add(new WindowButtons());

        borderPane.setTop(toolBar);

        primaryStage.setScene(new Scene(borderPane, 300, 250));
        primaryStage.show();
    }

    class WindowButtons extends HBox {
        public WindowButtons() {
            Button closeBtn = new Button("X");
            closeBtn.setOnAction(actionEvent -> Platform.exit());
            this.getChildren().add(closeBtn);
        }
    }
}
