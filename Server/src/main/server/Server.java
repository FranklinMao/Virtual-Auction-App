/*
 *  EE422C Final Project submission by
 *  Replace <...> with your actual data.
 *  <Franklin Mao>
 *  <fm8487>
 *  <16295>
 *  Spring 2020
 */
import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Observable {
    private HashMap<String, Item> items;
    private HashSet<String> usernames;
    public static void main(String[] args){

        new Server().runServer();
    }

    public Server() {
        items = new HashMap<>();
        usernames = new HashSet<>();
    }

    private void runServer() {
        try {
            Init();
            setUpNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void Init() throws FileNotFoundException {
        File file = new File("src/main/server/Items.txt");
        Scanner scan = new Scanner(file);
        String line;

        Gson gson = new Gson();
        while(scan.hasNextLine()) {
            line = scan.nextLine();
            Item initialItem = gson.fromJson(line, Item.class);
            items.put(initialItem.getName(), initialItem);

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
        Command command = gson.fromJson(input, Command.class);
        Item item = gson.fromJson(input, Item.class);
        System.out.println(command.toString());
        if(command.getCommand()!= null) {
            if (command.getCommand().equals("USER:")) {
                if (!usernames.contains(command.getUsername())) {
                    usernames.add(command.getUsername());
                    System.out.println(command.getUsername());
                }
            }
            else if(command.getCommand().equals("BID:")) {
                Item selectedItem = items.get(command.getItemName());
                System.out.println(command.getPrice());
                selectedItem.setCurrPrice(command.getPrice());
                if(selectedItem.getCurrPrice() >= selectedItem.getMaxPrice()) {

                }
            }
        }
        if(item.getName()!=null) {
            System.out.println(item.toString());

                usernames.add(item.getDescription());

        }
        this.setChanged();              //server has changed
        this.notifyObservers(items);         //update all clients
    }
}

