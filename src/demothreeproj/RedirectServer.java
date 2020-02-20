/*
 * 
 */
package demothreeproj;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yoraaf
 *
 * This server gets launched when a client connects to the chat server. This
 * will pass the IP of the host to anyone trying to connect to this client that
 * way a user can enter any member IP to find and connect to the host.
 */
public class RedirectServer {

    private static final int PORT = 59002;
    private static String IP;

    public RedirectServer(String arg) throws Exception {
        System.out.println("Redirect server is running to: "+arg);
        IP = arg;
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }

    }

    private static class Handler implements Runnable {

        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
                
                out.println("REDIRECT "+IP);
            } catch (Exception e) {
                 System.out.println(e);
            }
            
        }
    }

}
