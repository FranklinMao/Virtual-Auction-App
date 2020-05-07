package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static String host = "localhost";
    private BufferedReader fromServer;
    private PrintWriter toServer;

    public static void main(String[] args) {
        try {
            new Client().setUpNetworking();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        System.out.println(item.toString());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


        });
        Thread writerT = new Thread(() -> {
            //while (true) {
                Item item = new Item("test", 5.00, 10.00);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                sentToServer(gson.toJson(item));
            //}
        });
        readerT.start();
        writerT.start();
    }

    protected void sentToServer(String toJson) {
        System.out.println("Sending to server: " + toJson);
        toServer.println(toJson);
        toServer.flush();
    }
}
