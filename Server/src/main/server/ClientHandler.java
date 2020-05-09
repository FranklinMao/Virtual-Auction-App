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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class ClientHandler implements Runnable, Observer {
    private Server server;
    private Socket clientSocket;
    private BufferedReader fromClient;
    private PrintWriter toClient;
    private String username;

    public ClientHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        try {
            fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            toClient = new PrintWriter(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        String input;
        try {
            while ((input = fromClient.readLine()) != null) {
                System.out.println("From client:" + input);
                server.processRequest(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {
        this.sendToClient(arg);
    }

    protected synchronized void sendToClient(Object arg) {
        System.out.println("Sending to client");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        if(arg instanceof HashMap<?,?>) {   //send all items in server to client
            for (Map.Entry<?,?> i : ((HashMap<?, ?>) arg).entrySet()) {
                toClient.println(gson.toJson(i.getValue()));
            }
            toClient.flush();
        }

    }
}
