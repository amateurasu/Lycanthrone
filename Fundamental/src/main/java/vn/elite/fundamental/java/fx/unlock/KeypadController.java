package vn.elite.fundamental.java.fx.unlock;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller for the custom Keypad component - see 'Keypad.fxml' and 'Keypad.java'.
 */
public final class KeypadController implements Initializable {
    @FXML
    private Button del;
    @FXML
    private Button ok;
    @FXML
    private PasswordField display;

    private Callback<String, Boolean> validateCallback = null;

    // Handler for Button[Button[id=null, styleClass=button]] onAction
    // Handler for Button[fx:id="del"] onAction
    // Handler for Button[fx:id="ok"] onAction
    public void keyPressed(ActionEvent event) {
        if (event.getTarget() instanceof Button) {
            if (event.getTarget() == del && !display.getText().isEmpty()) {
                delete();
            } else if (event.getTarget() == ok) {
                validateCallback.call(display.getText());
                display.setText("");
            } else if (event.getTarget() != del) {
                append(((Button) event.getTarget()).getText());
            }
            event.consume();
        }
    }

    private void delete() {
        display.setText(display.getText().substring(0, display.getText().length() - 1));
    }

    private void append(String s) {
        display.appendText(s);
    }

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert del != null : "fx:id=\"del\" was not injected: check your FXML file 'Keypad.fxml'.";
        assert ok != null : "fx:id=\"ok\" was not injected: check your FXML file 'Keypad.fxml'.";
        assert display != null : "fx:id=\"password\" was not injected: check your FXML file 'Keypad.fxml'.";
    }

    void setValidateCallback(Callback<String, Boolean> validateCB) {
        validateCallback = validateCB;
    }
}
