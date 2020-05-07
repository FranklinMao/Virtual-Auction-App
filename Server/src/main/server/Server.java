import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

public class Server extends Observable {
    public static void main(String[] args){

        new Server().runServer();
    }

    private void runServer() {
        try {
            setUpNetworking();
        } catch (Exception e) {
            e.printStackTrace();
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
        this.notifyObservers(item);         //update all clients
    }
}

