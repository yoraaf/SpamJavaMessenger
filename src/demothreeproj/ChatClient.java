package demothreeproj;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.Font;
import static java.awt.Font.PLAIN;
import java.awt.GridLayout;
import java.awt.TextField;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * A simple Swing-based client for the chat server. Graphically it is a frame
 * with a text field for entering messages and a textarea to see the whole
 * dialog.
 *
 * The client follows the following Chat Protocol. When the server sends
 * "SUBMITNAME" the client replies with the desired screen name. The server will
 * keep sending "SUBMITNAME" requests as long as the client submits screen names
 * that are already in use. When the server sends a line beginning with
 * "NAMEACCEPTED" the client is now allowed to start sending the server
 * arbitrary strings to be broadcast to all chatters connected to the server.
 * When the server sends a line beginning with "MESSAGE" then all characters
 * following this string should be displayed in its message area.
 */
public class ChatClient {

    private static final int PORT = 59002;

    String serverAddress;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Spam");
    JTextField textField = new JTextField(50);
    String username;
    //String IPToConnectTo;

    JTextArea messageArea = new JTextArea(16, 50);

    /**
     * Constructs the client by laying out the GUI and registering a listener
     * with the textfield so that pressing Return in the listener sends the
     * textfield contents to the server. Note however that the textfield is
     * initially NOT editable, and only becomes editable AFTER the client
     * receives the NAMEACCEPTED message from the server.
     */
    Font defFont = new Font("Lucida Sans Typewriter", PLAIN, 15);

    public static String[] IPs = new String[]{"127.0.0.1"}; //this list will contain all IPs that the chat client will try to connect to

    public ImageIcon iconImage = new ImageIcon(getClass().getResource("img/spam.png"));
    //self made icon

    public static void main(String[] args) throws Exception {

        String[] userNameIP = startPopup();
        String IPToConnectTo = userNameIP[1];

        try {
            Socket socket = new Socket(userNameIP[1], PORT);
            Scanner tempIn = new Scanner(socket.getInputStream());
            if (!tempIn.nextLine().contains("SUBMITNAME") || !tempIn.nextLine().contains("REDIRECT")) {
                makeServer(); //make your own server if the one entered doesn't exist
                IPToConnectTo = "127.0.0.1"; //change the ip to connect to, to the localhost IP
            } //else, don't make your own server and join the IP entered.
            //later this can contain an IF statement for if the person isn't the host but IS a member
        } catch (ConnectException ex) { //this checks if that IP accepts connections 
            System.out.println("Server IP not reachable");
            makeServer();
            IPToConnectTo = "127.0.0.1";
        } catch (Exception ex) {
            System.out.println("Entered IP has wrong format");
            makeServer();
            IPToConnectTo = "127.0.0.1";
        }

        ChatClient client = new ChatClient(userNameIP[0], IPToConnectTo);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();

    }

    private static void makeServer() {
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
    }

    private void makeRedirectServer() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    RedirectServer server = new RedirectServer(serverAddress);
                } catch (Exception ex) {
                    Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                }
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

        int result = JOptionPane.showConfirmDialog(null, inputFields,
                "Please enter username and IP", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            username = usernameField.getText();
            IPToConnectTo = IPField.getText();
            System.out.println("username: " + usernameField.getText());
            System.out.println("IP: " + IPField.getText());
        } else if (result == -1) {
            System.exit(0);
        }
        return new String[]{username, IPToConnectTo};
        // Send on enter then clear to prepare for next message
    }

    public ChatClient(String user, String IP) {
        username = user;
        serverAddress = IP;
        textField.setFont(defFont);
        messageArea.setFont(defFont);
        System.out.println("test");
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();
        frame.setIconImage(iconImage.getImage());

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });

        //code below is to ask for username and IP
    }

    private void run() throws IOException {
        try {

            Socket socket = new Socket(serverAddress, PORT);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) {
                String line = in.nextLine();
                System.out.println(line);
                if (line.startsWith("SUBMITNAME")) {
                    out.println(username);
                } else if (line.startsWith("REDIRECT")) {
                    //if the IP isn't the host it will tell us the host IP. Connect to this IP
                    //and redefine socket, in and out 
                    serverAddress = line.substring(9);
                    socket = new Socket(serverAddress, PORT);
                    in = new Scanner(socket.getInputStream());
                    out = new PrintWriter(socket.getOutputStream(), true);
                    makeRedirectServer();
                } else if (line.startsWith("NAMEACCEPTED")) {
                    this.frame.setTitle("Spam - " + line.substring(13));
                    textField.setEditable(true);
                } else if (line.startsWith("MESSAGE")) {
                    messageArea.append(line.substring(8) + "\n");
                }
            }
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

}
