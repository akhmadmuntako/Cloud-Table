package com.example.cloudtable;

import android.util.Log;

import com.example.cloudtable.Activity.MainActivity;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Lenovo on 11/08/2016.
 */
public class NetworkServer implements Runnable {
    private final int port;
    String ip;

    /**
     * Creates a server with default port 9876
     */
    public NetworkServer() {
        this(9000);
        ip = MainActivity.getIP();
    }

    /**
     * Creates a server with the specified port
     *
     * @param port Port to listen on
     */
    public NetworkServer(int port) {
        this.port = port;
    }

    public void run() {
        ServerSocket socket = null;
        boolean listening = true;

        // Open the socket to listen for connections
        try {
            socket = new ServerSocket(port);
//            socket.setSoTimeout(70000);
            Log.w("ServerSocket", "start");
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }

        // Wait for connections and spawn RequestHandler threads
        while (listening) {
            try {
                new Thread(new RequestHandler(socket.accept())).start();
                Log.w("listening", "start : listen from "+ socket.accept().getInetAddress());
            } catch (IOException e) {
                e.printStackTrace();
                Log.w("listening", " cannot start"+ " because "+ e.getMessage());
            }
        }
        Log.w("listening", "false");

        // Close the socket (won't ever get here under normal execution)
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}