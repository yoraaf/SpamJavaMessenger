package demothreeproj;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public final class ChatClient {

    private static final int PORT = 59002;

    private String serverAddress;
    private Scanner in;
    private PrintWriter out;
   
    private String username;
    //String IPToConnectTo;


    private String localIP = "";
    private ArrayList<String> memberIPs = new ArrayList<>();
    private String[] ipArray;
    //private RedirectServer redirectServer;
    private boolean isHost = false;
    private GUI gui;


    public ChatClient() {

        String[] userNameIP = startPopup();
        String IPToConnectTo = userNameIP[1];
        username = userNameIP[0];

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
                //makeRedirectServer();
            } else if (line.contains("SUBMITNAME")) {
                serverAddress = IPToConnectTo;
                //makeRedirectServer();
            } else if (!line.contains("SUBMITNAME")) { //else, don't make your own server and join the IP entered.
                isHost = true;
                makeServer(); //make your own server if the one entered doesn't exist
                IPToConnectTo = "127.0.0.1"; //change the ip to connect to, to the localhost IP
            }

        } catch (ConnectException ex) { //this checks if that IP accepts connections 
            //Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            isHost = true;
            System.out.println("Server IP not reachable");
            makeServer();
            IPToConnectTo = "127.0.0.1";

        } catch (Exception ex) {
            //Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            isHost = true;
            System.out.println("Entered IP has wrong format");
            makeServer();
            IPToConnectTo = "127.0.0.1";

        }
        serverAddress = IPToConnectTo;
        //userInterface();
        gui = new GUI(this);

        //This checks for when the members list is requested
        
        try {
            run();
        } catch (Exception ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    


    private void makeServer() {

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ChatServer server = new ChatServer();
                } catch (Exception ex) {
                    Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
        JOptionPane.showMessageDialog(null, "You are the coordinator");
    }

    private void makeRedirectServer() {
        Thread t = new Thread(() -> {
            try {
                RedirectServer redirectServer = new RedirectServer(serverAddress);
            } catch (Exception ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t.start();
    }

    private static String[] startPopup() {
        String username = "";
        String IPToConnectTo = "";
        JTextField usernameField = new JTextField(5);
        JTextField IPField = new JTextField(5);

        JPanel inputFields = new JPanel();
        inputFields.setLayout(new GridLayout(0, 2));
        inputFields.add(new JLabel("Username: "));
        inputFields.add(usernameField);
        inputFields.add(new JLabel("Server IP: "));
        inputFields.add(IPField);
        while (true) {
            int result = JOptionPane.showConfirmDialog(null, inputFields,
                    "Please enter username and IP", JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                username = usernameField.getText();
                IPToConnectTo = IPField.getText();
                System.out.println("username: " + usernameField.getText());
                System.out.println("IP: " + IPField.getText());
                if (username == null || username.isEmpty() || username.contains(";") || username.contains("~") || username.contains("[") || username.contains("]") || username.contains("(")) {
                    JOptionPane.showMessageDialog(null, "Please enter a name that doesn't contain '~', ';', '(', '[' or ']'");
                } else {
                    break;
                }
            } else if (result == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
                break;
            }
        }
        return new String[]{username, IPToConnectTo};
        // Send on enter then clear to prepare for next message
    }

    private void run() throws Exception {
        try { //doesn't seem really necesarry

            Socket socket = new Socket(serverAddress, PORT);
            //s1.setSoTimeout(200);
            //s1.connect(new InetSocketAddress("192.168.1." + i, 1254), 200);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            int submitnameCounter = 0;
            while (in.hasNextLine()) {
                String line = in.nextLine();
                System.out.println(line);
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
//                    socket = new Socket(serverAddress, PORT);
//                    in = new Scanner(socket.getInputStream());
//                    out = new PrintWriter(socket.getOutputStream(), true);
                    run();
                    //makeRedirectServer();
                } else if (line.startsWith("NAMEACCEPTED")) {
                    if (!isHost) {
                        makeRedirectServer();
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
            //frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            RedirectServer.closeServer();
            System.out.println("");
            for (int i = 1; i < ipArray.length; i++) {
                String IPToConnectTo = ipArray[i];
                if (ipArray[i].equals(localIP)) {
                    isHost = true;
                    IPToConnectTo = "127.0.0.1";
                    makeServer();
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
