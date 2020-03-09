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

public class ChatServer {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final int PORT = 59002;
    private static ArrayList<String> userData = new ArrayList<String>();

    // All client names, so we can check for duplicates upon registration.
    private static Set<String> names = new HashSet<>();

    // The set of all the print writers for all the clients, used for broadcast.
    private static Set<PrintWriter> writers = new HashSet<>();
    private static ArrayList<PrintWriter> writerList = new ArrayList<>();

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
     * The client handler.
     */
    private static class Handler implements Runnable {

        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private String clientIP;
        private int clientPort;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                // Keep requesting a name until we get a unique one.
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    synchronized (names) {
                        if (!name.isEmpty() && !names.contains(name)) {
                            names.add(name);
                            clientIP = socket.getInetAddress().getHostAddress();
                            clientPort = socket.getLocalPort();
                            if (clientIP.equals("127.0.0.1")) {
                                clientIP = InetAddress.getLocalHost().getHostAddress();
                                userData.add("(C) " + name + ";" + clientIP + ";" + clientPort);
                            } else {
                                userData.add(name + ";" + clientIP + ";" + clientPort);
                            }

                            out.println("NAMEACCEPTED " + name);
                            broadcastToAll("MESSAGE " + getTime() + name + " has joined");
                            writers.add(out);
                            writerList.add(out);
                            updateMembers();
                            break;
                        }
                    }
                }

                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.nextLine();
                    String intendedUsr = "";
                    if (input.startsWith("[") && input.contains("]")) {
                        System.out.println("");
                        intendedUsr = input.substring(input.indexOf('[')+1);
                        intendedUsr = intendedUsr.substring(0, intendedUsr.indexOf(']'));
                        System.out.println(name + " sending PM to " + intendedUsr);
                        for (String user : userData) { //this loop goes through al the user data
                            //here it singles out the name from the data
                            String userName = user.substring(0, user.indexOf(';'));
                            if (userName.equals(intendedUsr)) { //here it checks if the user name is equal to the intended user
                                int userIndex = userData.indexOf(user); //get index
                                PrintWriter wrt = writerList.get(userIndex);
                                wrt.println("MESSAGE " + getTime() + name + ": " +input);
                                out.println("MESSAGE " + getTime() + name + ": " +input);
                            }
                        }
                    }else{
                        broadcastToAll("MESSAGE " + getTime() + name + ": " + input);
                    }

                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                    writerList.remove(out);
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
                } catch (Exception e) {
                    System.out.println("Socket closed");
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
                    Collections.swap(writerList, i, 0);
                    break;
                }
            }
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
