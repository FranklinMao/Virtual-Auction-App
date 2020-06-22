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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class Server extends Observable {
    private ItemsDB itemsDB;
    private HashMap<String, Item> items;
    private HashSet<String> usernames;
    private String historyLog = "";
    public static void main(String[] args){

        new Server().runServer();
    }

    public Server() {
        items = new HashMap<>();
        usernames = new HashSet<>();
    }

    private void runServer() {
        try {
            itemsDB = new ItemsDB();
            Init();
            setUpNetworking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void Init() throws IOException {
        InputStream file = ClassLoader.getSystemResourceAsStream("Items.txt");
        BufferedReader scan = new BufferedReader(new InputStreamReader(file));
        String line;

        Gson gson = new Gson();
        while((line = scan.readLine()) != null) {
            Item initialItem = gson.fromJson(line, Item.class);
            items.put(initialItem.getName(), initialItem);
            itemsDB.insert(initialItem);
        }
        itemsDB.listItems();
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

    protected synchronized void processRequest(String input) throws NoSuchAlgorithmException {
        Gson gson = new Gson();
        Command command = gson.fromJson(input, Command.class);
        Item item = gson.fromJson(input, Item.class);
        System.out.println(command.toString());
        if(command.getCommand()!= null) {
            if (command.getCommand().equals("USER:")) {
                if (!itemsDB.hasUser(command.getUsername())) {
                    usernames.add(command.getUsername());
                    SecureRandom random = new SecureRandom();
                    byte[] salt = new byte[20];
                    random.nextBytes(salt);
                    byte[] pass = command.getItemName().getBytes();
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

                    byte[] saltedPassword = (pass.toString() + salt.toString()).getBytes();
//                    System.arraycopy(pass, 0, saltedPassword, 0, pass.length);
//                    System.arraycopy(salt, 0, saltedPassword, pass.length, salt.length);
                    byte[] passHash = messageDigest.digest(saltedPassword);
                    System.out.println(passHash);
                    itemsDB.storeID(command.getUsername(), passHash, salt);
                    System.out.println(command.getItemName());
                    this.setChanged();
                    this.notifyObservers(new Command("VALID:", command.getUsername(), "", null));
                    this.setChanged();
                    this.notifyObservers(new Command("LOG:", "", historyLog, 0.00));
                }
//                else if (itemsDB.hasUser(command.getUsername()) && itemsDB.passwordMatch(command.getUsername(), command.getItemName())) {
//                    this.setChanged();
//                    this.notifyObservers(new Command("VALID:", command.getUsername(), "", null));
//                    this.setChanged();
//                    this.notifyObservers(new Command("LOG:", "", historyLog, 0.00));
//                }
                else {
                    this.setChanged();
                    this.notifyObservers(new Command("INVALID:", command.getUsername(), "", null));
                }
            }
            else if(command.getCommand().equals("BID:")) {
                if(command.getPrice() <= itemsDB.getItem(command.getItemName()).getCurrPrice()) //check if bid is valid on server side
                    return;
                itemsDB.updateItem(command.getItemName(), command.getPrice());
                Item selectedItem = itemsDB.getItem(command.getItemName());
                System.out.println(command.getPrice());
                if(selectedItem.getCurrPrice() >= selectedItem.getMaxPrice()) {
                    itemsDB.removeItem(selectedItem.getName());
                   this.setChanged();
                   this.notifyObservers(new Command("SELL:", command.getUsername(), selectedItem.getName(), selectedItem.getCurrPrice()));
                   historyLog += selectedItem.getName() + " has been sold to " + command.getUsername() + " for $" + command.getPrice() + "\n";
                }
                else {
//                    this.setChanged();
//                    this.notifyObservers(new Command("BID:", command.getUsername(), selectedItem.getName(), command.getPrice()));
                    historyLog += (command.getUsername() + " bid $" + command.getPrice() + " for " + command.getItemName() + "\n");
                }
                this.setChanged();
                this.notifyObservers(new Command("LOG:", "", historyLog, 0.00));
                System.out.println("new history");
            }
        }
        else if(item.getName()!=null) {
            System.out.println(item.toString());

                usernames.add(item.getDescription());

        }
        System.out.println(itemsDB.listItems());
        this.setChanged();              //server has changed
        this.notifyObservers(itemsDB);         //update all clients
    }
}

