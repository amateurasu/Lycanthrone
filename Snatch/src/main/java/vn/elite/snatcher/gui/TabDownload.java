package vn.elite.snatcher.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabDownload implements Initializable {
    @FXML
    public ScrollPane scrContent;
    @FXML
    public VBox vbx;
    @FXML
    private TextField txtDownload;
    @FXML
    private Button btnDownload;
    @FXML
    private Label lblDownload;
    private Map<String, String> map = new HashMap<String, String>() {{
        put("fb.com", "FACEBOOK");
        put("www.facebook.com", "FACEBOOK");
        put("imgur.com", "IMGUR");
        put("", "FACEBOOK");
        put("", "FACEBOOK");
    }};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setListener();
    }

    private void setListener() {
        txtDownload.focusedProperty().addListener((ov, onHidden, onShown) -> {
            try {
                String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                txtDownload.setText(data);
            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void analyze() {
        String addr = txtDownload.getText();
        try {
            if (!analyzeLink(addr) && !analyzeFile(addr)) {
                new Alert(Alert.AlertType.INFORMATION) {{
                    setTitle("Boohoo");
                    setHeaderText(null);
                    setContentText("Are you kidding me or WHAT!?? That's not even a valid link!!!");
                }}.showAndWait();
            }
        } catch (Exception e) {

        }
    }

    private boolean analyzeLink(String link) {
        Pattern pattern = Pattern.compile("^(?:(?:https?|ftp)://)?(\\w+([\\-.]\\w+)*\\.[a-z0-9]{2,5}(:[0-9]{1,5})?).*$");
        Matcher matcher = pattern.matcher(link);
        if (!matcher.matches()) {
            return false;
        }
        String s = map.get(matcher.group(1));
        System.out.println(s);

        vbx.getChildren().add(new DownloadProgress() {{

        }});
        return map.containsKey(matcher.group(1));
    }

    private boolean analyzeFile(String file) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(file));
        return true;
    }
}
