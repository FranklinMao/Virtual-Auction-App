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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    public Label userField;
    public ImageView itemImage;
    public AudioClip audioClip;
    public MediaPlayer mediaPlayer;

    public void initialize() {
        System.out.println("controller created");
        Pattern pattern = Pattern.compile("\\d*\\.?\\d{0,2}");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        bidField.setTextFormatter(formatter);
        historyLog = "";
        itemsList.setCellFactory(param -> new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item == null || item.getName() == null) {
                    setText(null);
                }
                else if (item.getDescription().equals("SOLD!")) {
                    setText(item.getName() + " has been " + item.getDescription());
                    setTextFill(Color.RED);
                }
                else {
                    setText(item.getName() + ", Description:" + item.getDescription() + ", Min Price: $" + item.getMinPrice() + ", Current Price: $" + item.getCurrPrice());
                }
            }
        });
        Media media = new Media(getClass().getResource("/REGISTER.wav").toString());
        audioClip = new AudioClip(getClass().getResource("/REGISTER.wav").toString());
        audioClip.stop();
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
    }

    public synchronized void quitButton(ActionEvent event) throws IOException, InterruptedException {
        Client.runThread = false;
        notify();
        wait();

        Client.quit();
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
        if(bidAmount <= selectedItem.getCurrPrice() || bidAmount < selectedItem.getMinPrice()) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Enter a higher bid amount!", ButtonType.OK);
            a.showAndWait();
            return;        //check if bid amount is valid
        }


        request = new Command("BID:", username, selectedItem.getName(), bidAmount);
        notify();   //tells the writer thread to resume and send to server
        //historyLog += ("You(" + username +") " + "bid $" + bidAmount + " for " + selectedItem.getName() + "\n");
//        mediaPlayer.seek(Duration.ZERO);
//        mediaPlayer.play();
        audioClip.play();
        updateLog();
    }


    public void updateLog() {
        historyField.setText(historyLog);
    }

    public void updateImage(MouseEvent mouseEvent) {
        Item selectedItem = itemsList.getSelectionModel().getSelectedItem();
        if(selectedItem == null) return;
        try {
            itemImage.setImage(new Image(getClass().getResource("/" + selectedItem.getName() + ".jpg").toString(), true));
        } catch (Exception e) {
            itemImage.setAccessibleText("Image not found");
        }
    }
}
