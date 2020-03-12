package demothreeproj;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
/**
 *
 * @author SPAM
 */
public final class ChatClient {

    private int PORT = 59002;

    private String serverAddress;
    private Scanner in;
    private PrintWriter out;
    private String username;
    private String localIP = "";
    private String[] ipArray;
    private boolean isHost = false;
    private GUI gui;

    public ChatClient() {
        gui = new GUI(this);
        String[] userNameIP = gui.startPopup();
        String IPToConnectTo = userNameIP[1]; //this is a temp var 
        username = userNameIP[0];
        PORT = Integer.parseInt(userNameIP[2]);

        try {
            localIP = InetAddress.getLocalHost().getHostAddress();
            Socket socket = new Socket(IPToConnectTo, PORT);
            Scanner tempIn = new Scanner(socket.getInputStream());
            String line = tempIn.nextLine();
            if (line.contains("REDIRECT")) {
                serverAddress = line.substring(9);
                socket = new Socket(serverAddress, PORT);
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
            } else if (line.contains("SUBMITNAME")) {
                serverAddress = IPToConnectTo;
            } else if (!line.contains("SUBMITNAME")) { //else, don't make your own server and join the IP entered.
                isHost = true;
                makeServer(PORT); //make your own server if the one entered doesn't exist
                IPToConnectTo = "127.0.0.1"; //change the ip to connect to, to the localhost IP
            }

        } catch (ConnectException ex) { //this checks if that IP accepts connections 
            isHost = true;
            System.out.println("Server IP not reachable");
            makeServer(PORT);
            IPToConnectTo = "127.0.0.1";

        } catch (Exception ex) {
            isHost = true;
            System.out.println("Entered IP has wrong format");
            makeServer(PORT);
            IPToConnectTo = "127.0.0.1";

        }
        serverAddress = IPToConnectTo;

        try {
            run();
        } catch (Exception ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void makeServer(int port) {

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ChatServer server = new ChatServer(port);
                } catch (Exception ex) {
                    Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
        JOptionPane.showMessageDialog(null, "You are the coordinator");
    }

    private void makeRedirectServer(String serverIP, int port) {
        Thread t = new Thread(() -> {
            try {
                RedirectServer redirectServer = new RedirectServer(serverIP, port);
            } catch (Exception ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t.start();
    }

    private void run() throws Exception {
        try { //doesn't seem really necesarry
            Socket socket = new Socket(serverAddress, PORT);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            int submitnameCounter = 0;
            while (in.hasNextLine()) {
                String line = in.nextLine();
                //System.out.println(line);
                if (line.startsWith("SUBMITNAME")) {
                    if (submitnameCounter > 0) {
                        while (true) {
                            username = JOptionPane.showInputDialog("Username taken. Try again.");

                            if (username == null || username.isEmpty() || username.contains(";") || username.contains("~") || username.contains("[") || username.contains("]") || username.contains("(")) {
                                JOptionPane.showMessageDialog(null, "Please enter a name that doesn't contain '~', ';', '[' or ']'");
                            } else {
                                break;
                            }
                        }
                    }
                    out.println(username);
                    submitnameCounter++;
                } else if (line.startsWith("REDIRECT")) {
                    //if the IP isn't the host it will tell us the host IP. Connect to this IP
                    //and redefine socket, in and out 
                    serverAddress = line.substring(9);
                    run();
                } else if (line.startsWith("NAMEACCEPTED")) {
                    if (!isHost) {
                        makeRedirectServer(serverAddress, PORT);
                    }
                    gui.setTitle("Spam - " + line.substring(13) + " " + localIP);
                    gui.setAllowedToMsg(true); //allows the user to use the message box
                } else if (line.startsWith("MESSAGE")) {
                    gui.addMessage(line.substring(8) + "\n");
                } else if (line.startsWith("MEMBERS")) {
                    updateMemberList(line.substring(8));
                }
            }

        } finally {
            RedirectServer.closeServer();
            for (int i = 1; i < ipArray.length; i++) {
                String IPToConnectTo = ipArray[i];
                if (ipArray[i].equals(localIP)) {
                    isHost = true;
                    IPToConnectTo = "127.0.0.1";
                    makeServer(PORT);
                }

                Socket newSocket = new Socket();
                newSocket.setSoTimeout(6000);
                newSocket.connect(new InetSocketAddress(IPToConnectTo, PORT), 6000);
                Scanner tempIn = new Scanner(newSocket.getInputStream());
                if (tempIn.hasNextLine()) {
                    serverAddress = IPToConnectTo;
                    run();
                    break;
                }
            }
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    private void updateMemberList(String str) {
        String[] members = str.split("~");
        String listWithReturn = "";
        ipArray = new String[members.length];

        for (int i = 0; i < members.length; i++) {
            ipArray[i] = members[i].split(";")[1]; //get the i-th member, split this, and get the second element, which is the IP
            listWithReturn += members[i] + "\n";
            System.out.println(i + ": " + ipArray[i]);
        }

        listWithReturn = listWithReturn.replace(";", "    ");
        gui.updateMembersList(listWithReturn);
    }
}
