/*
 *  EE422C Final Project submission by
 *  Replace <...> with your actual data.
 *  <Franklin Mao>
 *  <fm8487>
 *  <16295>
 *  Spring 2020
 */
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Observable {
    private HashSet<Item> items;
    public static void main(String[] args){

        new Server().runServer();
    }

    public Server() {
        items = new HashSet<>();
    }

    private void runServer() {
        try {
            Init();
            setUpNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Init() throws FileNotFoundException {
        File file = new File("src/main/server/Items.txt");
        Scanner scan = new Scanner(file);
        String line;

        Gson gson = new Gson();
        while(scan.hasNextLine()) {
            line = scan.nextLine();
            items.add(gson.fromJson(line, Item.class));

        }

    }

    private void setUpNetworking() throws Exception {
        ServerSocket serverSocket = new ServerSocket(4242);
        while(true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connecting to ..." + clientSocket);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ClientHandler handler = new ClientHandler(this, clientSocket);
            this.addObserver(handler);
            Thread t = new Thread(handler);
            t.start();

        }
    }

    protected void processRequest(String input) {
        Gson gson = new Gson();
        Item item = gson.fromJson(input, Item.class);
        System.out.println(item.toString());
        this.setChanged();              //server has changed
        this.notifyObservers(items);         //update all clients
    }
}

