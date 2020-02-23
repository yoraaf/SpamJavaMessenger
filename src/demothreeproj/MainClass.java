/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demothreeproj;

import java.awt.Font;
import static java.awt.Font.PLAIN;
import javax.swing.ImageIcon;

/**
 *
 * @author yoraa
 */
public class MainClass {
    
    //usually constants would have full caps names, but since this class will only have constants, it's already implied
    public static final Font chatDefFont = new Font("Lucida Sans Typewriter", PLAIN, 15);

    public static final ImageIcon iconImage = new ImageIcon(MainClass.class.getResource("img/spam.png"));
    //self made icon
    public static void main(String[] args){
        ChatClient client = new ChatClient();
    }
}
