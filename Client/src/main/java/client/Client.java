package client;
/*
 *  EE422C Final Project submission by
 *  Replace <...> with your actual data.
 *  <Franklin Mao>
 *  <fm8487>
 *  <16295>
 *  Spring 2020
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;

public class Client extends Application {
    private static String host = "localhost";
    private BufferedReader fromServer;
    private PrintWriter toServer;
    private Controller controller;
    private String username;
    private HashSet<Item> items = new HashSet<>();
    public static void main(String[] args) {

        launch(args);
    }

    private void setUpNetworking() throws IOException {
        Socket socky = new Socket(host, 4242);
        System.out.println("Connecting to..." + socky);
        fromServer = new BufferedReader(new InputStreamReader(socky.getInputStream()));
        toServer = new PrintWriter(socky.getOutputStream());
        Thread readerT = new Thread(() -> {
            String input;

            try {
                while (((input = fromServer.readLine()) != null)) {
                    System.out.println("From server: " + input);
                    Gson gson = new Gson();
                    Item item = gson.fromJson(input, Item.class);
                    if(!items.contains(item))           //TODO: Need to make sure it correctly detects duplicates, hashmap?
                        items.add(item);
                    Platform.runLater(() -> controller.updateItems(items));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        });
        Thread writerT = new Thread(() -> {
            //while (true) {
            Item item = new Item("test", "test", 5.00, 10.00);

            sentToServer(item);
            //}
        });
        readerT.start();
        writerT.start();
    }

    protected void sentToServer(Item toJson) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        System.out.println("Sending to server: " + toJson);
        toServer.println(gson.toJson(toJson));
        toServer.flush();
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view.fxml"));

        Parent root = loader.load(); //load FXML info
        controller = loader.getController();

        Stage loginStage = new Stage();
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
        AnchorPane rootLayout = (AnchorPane) loginLoader.load();
        Scene loginScene = new Scene(rootLayout);
        loginStage.setScene(loginScene);
        loginStage.setAlwaysOnTop(true);
        loginStage.show();
        LoginController loginController = loginLoader.getController();
        loginController.loggedInProperty().addListener((obs, wasLoggedIn, isNowLoggedIn) -> {
            if(isNowLoggedIn) {
                username = loginController.usernameField.getText(); //set username to the inputted name at login
                loginStage.hide();
                sentToServer(new Item("username", username, 0,0));
            }
        });


        Scene scene = new Scene(root, 1200, 700); //set window size
        setUpNetworking();
        primaryStage.setTitle("Auction Client");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
