/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demothreeproj;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author rv1658f
 */
public class GUITest {

    GUI instance;
    public GUITest() {
    }

    @Before
    public void setUpClass() {
        instance = new GUI();
    }

    @After
    public void tearDownClass() {
    }

    /**
     * Test of setAllowedToMsg method, of class GUI.
     */
    @Test
    public void testSetAllowedToMsg() {
        System.out.println("setAllowedToMsg");
        boolean arg = false;
        boolean result =instance.setAllowedToMsg(arg);
        Assert.assertEquals(arg, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of setTitle method, of class GUI.
     */
    @Test
    public void testSetTitle() {
        System.out.println("setTitle");
        String title = "This Is A Title";
        String result = instance.setTitle(title);
        Assert.assertEquals(title, result);
    }

    /**
     * Test of updateMembersList method, of class GUI.
     */
    @Test
    public void testUpdateMembersList() {
        System.out.println("updateMembersList");
        String list = "Henk    172.0.0.1    59001";
        String result = instance.updateMembersList(list);
        Assert.assertEquals(result, list);
    }

    /**
     * Test of startPopup method, of class GUI.
     */
//    @Test
//    public void testStartPopup() {
//        
//        String usernameText = "Henk";
//        StringSelection stringSelection = new StringSelection(usernameText);
//        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//        clipboard.setContents(stringSelection, stringSelection);
//
//        try {
//            Robot robot = new Robot();
//            robot.keyPress(KeyEvent.VK_H);
//            robot.keyRelease(KeyEvent.VK_H);
//            robot.keyPress(KeyEvent.VK_CONTROL);
//            robot.keyPress(KeyEvent.VK_V);
//            robot.keyRelease(KeyEvent.VK_V);
//            robot.keyRelease(KeyEvent.VK_CONTROL);
//            robot.keyPress(KeyEvent.VK_ENTER);
//            robot.keyRelease(KeyEvent.VK_ENTER);
//        } catch (AWTException ex) {
//            Logger.getLogger(GUITest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        System.out.println("startPopup");
//        String[] expResult = new String[]{usernameText, "", "59001"};
//        String[] result = instance.startPopup();
//        Assert.assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//    }

}
