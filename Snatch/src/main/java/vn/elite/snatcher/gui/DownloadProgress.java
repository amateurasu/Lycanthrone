package vn.elite.snatcher.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import vn.elite.snatcher.Test;

import java.io.IOException;

public class DownloadProgress extends HBox {
    private final DownloadController controller;

    public DownloadProgress() {
        controller = load();
    }

    private DownloadController load() {
        final FXMLLoader loader = new FXMLLoader();
        loader.setRoot(this);
        loader.setClassLoader(Test.class.getClassLoader());
        loader.setLocation(Test.class.getResource("DownloadProgress.fxml"));

        try {
            final Object root = loader.load();
            assert root == this;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        final DownloadController keypadController = loader.getController();
        assert keypadController != null;
        return keypadController;
    }
}
