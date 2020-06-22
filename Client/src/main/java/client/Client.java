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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Client extends Application {
    private static String host = "localhost";
    private static BufferedReader fromServer;
    private static PrintWriter toServer;
    public static boolean runThread = true;
    private Controller controller;
    private LoginController loginController;
    private String username;
    private Boolean isNameValid = false;
    private Map<String, Item> items = new HashMap<>();
    private Thread readerT;
    private Thread writerT;

    public static void main(String[] args) {

        launch(args);
    }

    private void setUpNetworking() throws IOException {
        Socket socky = new Socket(host, 4242);
        System.out.println("Connecting to..." + socky);
        fromServer = new BufferedReader(new InputStreamReader(socky.getInputStream()));
        toServer = new PrintWriter(socky.getOutputStream());
        readerT = new Thread(() -> {
            String input;



                try {
                    while (!socky.isClosed() && !readerT.isInterrupted() && ((input = fromServer.readLine()) != null)) {
                        System.out.println(runThread);
                        if (!runThread) {
                            //controller.wait();
                            controller.notify();
                            return;
                        }
                        synchronized (this) {
                            System.out.println("From server: " + input);
                            Gson gson = new Gson();
                            Item item = gson.fromJson(input, Item.class);
                            Command command = gson.fromJson(input, Command.class);
                            //System.out.println(newRequest.toString());
                            if (item.getName() != null) {
                                System.out.println(item.toString());
                                items.put(item.getName(), item);        //TODO: Need to make sure it correctly detects duplicates, hashmap?
                            } else if (command.getCommand() != null) {
                                if (command.getCommand().equals("SELL:")) {
                                    Item soldItem = items.get(command.getItemName());
                                    soldItem.setDescription("SOLD!");
                                    controller.historyLog += soldItem.getName() + " has been sold to " + command.getUsername() + " for $" + command.getPrice() + "\n";
                                    //Platform.runLater(() -> controller.updateLog());
                                }
                                System.out.println(command.getCommand());
                                if (command.getCommand().equals("LOG:")) {
                                    Item bidItem = items.get(command.getItemName());
                                    controller.historyLog = command.getItemName();
                                    //controller.historyLog += (command.getUsername() + " bid $" + command.getPrice() + " for " + bidItem.getName() + "\n");
                                    Platform.runLater(() -> controller.updateLog());
                                }
                                if (command.getCommand().equals("INVALID:")) {
                                    synchronized (loginController) {
                                        loginController.loggedInProperty().set(false);
                                        loginController.notify();
                                    }
                                }
                                if (command.getCommand().equals("VALID:")) {
                                    synchronized (loginController) {
                                        loginController.loggedInProperty().set(true);
                                        loginController.notify();
                                    }
                                }
                            }

                            Platform.runLater(() -> controller.updateItems(items));
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Server is down!");
                }


        });
        writerT = new Thread(() -> {
            while (runThread) {

                System.out.println("writer is working");
                synchronized (controller) {
                    try {
                        controller.wait();
                        if(!runThread) {
                            controller.notify();
                            readerT.interrupt();
                            readerT.stop();     //TODO: change if necessary
                            return;
                        }

                        sentToServer(controller.request);


                    } catch (InterruptedException e) {
                        System.out.println("Server is down");
                        e.printStackTrace();
                    }
                }

            }

        });

        readerT.start();
        writerT.start();
    }

    protected static void sentToServer(Object toJson) {
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
        loginController = loginLoader.getController();
        loginController.loggedInProperty().addListener((obs, wasLoggedIn, isNowLoggedIn) -> {
            if (isNowLoggedIn) {
                System.out.println("a new username exists");
                username = loginController.usernameField.getText(); //set username to the inputted name at login
                String password = loginController.passwordField.getText();
                controller.username = username;
                try {
                    synchronized (loginController) {
                        sentToServer(new Command("USER:", username, password, 0));    //TODO: check if username is valid/no duplicates
                        loginController.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(loginController.loggedInProperty().get()) {
                    loginController.validLabel.setText("VALID USERNAME!");
                    controller.userField.setText(username);
                    loginStage.hide();
                }
                else {
                    System.out.println("INVALID USER IS TAKEN");
                    loginController.validLabel.setText("USERNAME IS INVALID/TAKEN!");
                }
            }
        });


        Scene scene = new Scene(root, 1200, 700); //set window size

        setUpNetworking();
        HBox hBox = (HBox) root.getChildrenUnmodifiable().get(0);
        root.autosize();
        hBox.autosize();
        primaryStage.setTitle("Virtual Auction Client");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static synchronized void quit() throws IOException {
        runThread = false;

        toServer.close();
        fromServer.close();

        Platform.exit();
    }


}
