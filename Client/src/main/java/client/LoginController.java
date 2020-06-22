package client;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {

    public TextField usernameField;
    public Button loginButton;
    public final BooleanProperty loggedIn = new SimpleBooleanProperty();
    public Label validLabel;
    public TextField passwordField;

    public BooleanProperty loggedInProperty() {
        return loggedIn;
    }

    public final Boolean isLoggedIn() {
        return loggedInProperty().get();
    }
    public void loginToClient(ActionEvent actionEvent) {
        loggedInProperty().set(true);
    }
}
