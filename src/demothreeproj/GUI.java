/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demothreeproj;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 *
 * @author rv1658f
 */
public class GUI {

    private ChatClient thisClient;
    private JTextField textField = new JTextField(50);
    private JButton membersListButton = new JButton();
    private JTextArea messageArea = new JTextArea(16, 50);
    private JScrollPane scrollPane;
    private JFrame frame = new JFrame("Spam");
    private JFrame membersListFrame = new JFrame("Members");
    private JTextArea membersListText = new JTextArea(16, 20);

    public GUI(ChatClient thisClient) {
        this.thisClient = thisClient;
        userInterface();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        membersListButton.addActionListener((ActionEvent e) -> {
            membersListFrame.setVisible(true);
        });
        textField.addActionListener((ActionEvent e) -> {
            thisClient.sendMessage(textField.getText());
            textField.setText("");
        });
    }
    public GUI() { //this only works for the tests so that they can access the other methods.
        userInterface();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        membersListButton.addActionListener((ActionEvent e) -> {
            membersListFrame.setVisible(true);
        });
    }

    private void userInterface() {
        //main chat window   
        textField.setFont(MainClass.chatDefFont);
        messageArea.setFont(MainClass.chatDefFont);
        membersListFrame.setFont(MainClass.listDefFont);
        membersListButton.setText("→");
        membersListButton.setPreferredSize(new Dimension(20, 15));
        membersListButton.setMargin(new Insets(1, 1, 1, 1));
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(membersListButton, BorderLayout.EAST);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        scrollPane = new JScrollPane(messageArea);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.pack();
        frame.setIconImage(MainClass.iconImage.getImage());

        //members window
        membersListText.setEditable(false);
        membersListFrame.add(new JScrollPane(membersListText));
        membersListFrame.setVisible(true);
        membersListFrame.setSize(400, frame.getHeight());
        membersListFrame.setLocation(frame.getX() + frame.getWidth(), frame.getY()); //make it appear next to the chat window instaed of behind
        membersListFrame.setIconImage(MainClass.iconImage.getImage());
        membersListText.setFont(MainClass.listDefFont);
    }

    public boolean setAllowedToMsg(boolean arg) {
        textField.setEditable(arg);
        return textField.isEditable();
    }

    public String setTitle(String title) {
        frame.setTitle(title);
        return frame.getTitle();
    }

    public void addMessage(String msg) {
        messageArea.append(msg);
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public String updateMembersList(String list) {
        membersListText.setText(list);
        return membersListText.getText();
    }

    public String[] startPopup() {
        String username = "";
        String IPToConnectTo = "";
        String port = "";
        JTextField usernameField = new JTextField(5);
        JTextField IPField = new JTextField(5);
        JTextField portField = new JTextField(5);
        portField.setText("59001");
        usernameField.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent e) {
                JComponent component = e.getComponent();
                component.requestFocusInWindow();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
        JPanel inputFields = new JPanel();
        inputFields.setLayout(new GridLayout(0, 2));
        inputFields.add(new JLabel("Username: "));
        inputFields.add(usernameField);
        inputFields.add(new JLabel("Server IP: "));
        inputFields.add(IPField);
        inputFields.add(new JLabel("Server PORT: "));
        inputFields.add(portField);
        while (true) {
            int result = JOptionPane.showConfirmDialog(null, inputFields,
                    "Please enter username and IP", JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                username = usernameField.getText();
                IPToConnectTo = IPField.getText();
                port = portField.getText();
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
        return new String[]{username, IPToConnectTo, port};
        // Send on enter then clear to prepare for next message
    }
}
