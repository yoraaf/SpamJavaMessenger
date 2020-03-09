/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demothreeproj;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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

    public void setAllowedToMsg(boolean arg) {
        textField.setEditable(arg);
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }

    public void addMessage(String msg) {
        messageArea.append(msg);
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
    
    public void updateMembersList(String list){
        membersListText.setText(list);
    }
}
