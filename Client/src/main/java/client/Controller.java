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
import javafx.scene.control.*;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;


public class Controller {
    public String username;
    public TextArea historyField;
    public ListView<Item> itemsList;
    public TextField bidField;
    public Button bidButton;
    public String historyLog;

    public Object request;
    public void initialize() {
        System.out.println("controller created");
        Pattern pattern = Pattern.compile("\\d*\\.?\\d{0,2}");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        bidField.setTextFormatter(formatter);
        historyLog = "";
    }
    public void quitButton(ActionEvent event) {
        System.exit(0);
    }



    public synchronized void updateItems(Map<String, Item> items) {
        ObservableMap<String, Item> map = FXCollections.observableHashMap();
        ObservableList<Item> observableItems = FXCollections.observableArrayList();


            for (Map.Entry<String, Item> entry : items.entrySet()) {
                observableItems.add(entry.getValue());
            }

        itemsList.setItems(observableItems);
    }

    public synchronized void sendBid(ActionEvent actionEvent) {
        if(bidField.getText().equals("")) return;
        double bidAmount = Double.parseDouble(bidField.getText());
        Item selectedItem = itemsList.getSelectionModel().getSelectedItem();
        if(selectedItem == null) return;
        if(selectedItem.getDescription().equals("SOLD!")) return;
        if(bidAmount <= selectedItem.getCurrPrice()) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Enter a higher bid amount!", ButtonType.OK);
            a.showAndWait();
            return;        //check if bid amount is valid
        }


        request = new Command("BID:", username, selectedItem.getName(), bidAmount);
        notify();   //tells the writer thread to resume and send to server
        historyLog += ("You(" + username +") " + "bid $" + bidAmount + " for " + selectedItem.getName() + "\n");
        updateLog();
    }


    public void updateLog() {
        historyField.setText(historyLog);
    }
}
