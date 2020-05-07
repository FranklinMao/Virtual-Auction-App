package client;
/*
 *  EE422C Final Project submission by
 *  Replace <...> with your actual data.
 *  <Franklin Mao>
 *  <fm8487>
 *  <16295>
 *  Spring 2020
 */
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class Controller {
    public TextArea historyField;
    public ListView<Item> itemsList;

    public void initialize() {
        System.out.println("controller created");
    }
    public void quitButton(ActionEvent event) {
        System.exit(0);
    }



    public synchronized void updateItems(Map<String, Item> items) {
        ObservableMap<String, Item> map = FXCollections.observableHashMap();
        ObservableList<Item> observableItems = FXCollections.observableArrayList();

        StringBuilder fullList = new StringBuilder();
        synchronized (this) {
            for (Map.Entry<String, Item> entry : items.entrySet()) {
                fullList.append(entry.toString()).append("\n");
                observableItems.add(entry.getValue());
            }
        }
        itemsList.setItems(observableItems);
        historyField.setText(fullList.toString());
    }
}
