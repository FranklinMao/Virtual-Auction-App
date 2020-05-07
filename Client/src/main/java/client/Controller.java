package client;
/*
 *  EE422C Final Project submission by
 *  Replace <...> with your actual data.
 *  <Franklin Mao>
 *  <fm8487>
 *  <16295>
 *  Spring 2020
 */
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;


public class Controller {
    public TextArea historyField;

    public void initialize() {
        System.out.println("controller created");
    }
    public void quitButton(ActionEvent event) {
        System.exit(0);
    }



    public void updateItems(HashSet<Item> items) {
        StringBuilder fullList = new StringBuilder();
        for(Item i : items) {
            fullList.append(i.toString()).append("\n");
        }
        historyField.setText(fullList.toString());
    }
}
