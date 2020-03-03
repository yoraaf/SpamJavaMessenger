package demothreeproj;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/* */
/**
 * A multithreaded chat room server. When a client connects the server requests
 * a screen name by sending the client the text "SUBMITNAME", and keeps
 * requesting a name until a unique one is received. After a client submits a
 * unique name, the server acknowledges with "NAMEACCEPTED". Then all messages
 * from that client will be broadcast to all other clients that have submitted a
 * unique screen name. The broadcast messages are prefixed with "MESSAGE".
 *
 * This is just a teaching example so it can be enhanced in many ways, e.g.,
 * better logging. Another is to accept a lot of fun commands, like Slack.
 */
public class ChatServer {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final int PORT = 59002;
    private static ArrayList<String> userData = new ArrayList<String>();

    // All client names, so we can check for duplicates upon registration.
    private static Set<String> names = new HashSet<>();
    private static int hostPositionInArray = 0;

    // The set of all the print writers for all the clients, used for broadcast.
    private static Set<PrintWriter> writers = new HashSet<>();

    public ChatServer() throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }
    }

    /**
     * The client handler task.
     */
    private static class Handler implements Runnable {

        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private String clientIP;
        private int clientPort;

        /**
         * Constructs a handler thread, squirreling away the socket. All the
         * interesting work is done in the run method. Remember the constructor
         * is called from the server's main method, so this has to be as short
         * as possible.
         */
        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Services this thread's client by repeatedly requesting a screen name
         * until a unique one has been submitted, then acknowledges the name and
         * registers the output stream for the client in a global set, then
         * repeatedly gets inputs and broadcasts them.
         */
        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                // Keep requesting a name until we get a unique one.
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    if (name == null || name.isEmpty()) {
                        return;
                    }
                    synchronized (names) {
                        if (!name.isEmpty() && !names.contains(name)) {
                            names.add(name);
                            clientIP = socket.getInetAddress().getHostAddress();
                            clientPort = socket.getLocalPort();
                            if (clientIP.equals("127.0.0.1")) {
                                clientIP = InetAddress.getLocalHost().getHostAddress();
                                userData.add("(C) " + name + ";" + clientIP + ";" + clientPort);
                                hostPositionInArray = userData.size() - 1;
                            } else {
                                userData.add(name + ";" + clientIP + ";" + clientPort);
                            }

                            out.println("NAMEACCEPTED " + name);
                            broadcastToAll("MESSAGE " + getTime() + name + " has joined");
                            writers.add(out);
                            updateMembers();
                            break;
                        }
                    }
                }

                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.nextLine();
//                    if (input.toLowerCase().startsWith("/quit")) {
//                        return;
//                    }
                    broadcastToAll("MESSAGE " + getTime() + name + ": " + input);
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                if (name != null) {
                    System.out.println(name + " is leaving");
                    names.remove(name);
                    for (String member : userData) {
                        if (member.contains(name)) {
                            userData.remove(member);
                            break;
                        }
                    }
                    broadcastToAll("MESSAGE " + getTime() + name + " has left");
                    try {
                        updateMembers();
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        public void broadcastToAll(String str) {
            for (PrintWriter writer : writers) {
                writer.println(str);
            }
        }

        private void updateMembers() throws UnknownHostException {
            String serverIP = InetAddress.getLocalHost().getHostAddress();
            for (int i = 0; i < userData.size(); i++) {
                if (userData.get(i).contains(serverIP)) {
                    Collections.swap(userData, i, 0);
                    break;
                }
            }
            //Collections.swap(userData, hostPositionInArray, 0);
            String membersString = "";
            for (String member : userData) {
                membersString += member + "~";

            }

            broadcastToAll("MEMBERS " + membersString);
        }

        private String getTime() {
            return "[" + dtf.format(LocalTime.now()) + "] ";
        }
    }
}
