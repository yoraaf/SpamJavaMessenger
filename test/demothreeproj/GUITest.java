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
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
//    @Test
//    public void testSetAllowedToMsg() {
//        System.out.println("setAllowedToMsg");
//        boolean arg = false;
//        GUI instance = null;
//        instance.setAllowedToMsg(arg);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setTitle method, of class GUI.
//     */
//    @Test
//    public void testSetTitle() {
//        System.out.println("setTitle");
//        String title = "";
//        GUI instance = null;
//        instance.setTitle(title);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addMessage method, of class GUI.
//     */
//    @Test
//    public void testAddMessage() {
//        System.out.println("addMessage");
//        String msg = "";
//        GUI instance = null;
//        instance.addMessage(msg);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of updateMembersList method, of class GUI.
//     */
//    @Test
//    public void testUpdateMembersList() {
//        System.out.println("updateMembersList");
//        String list = "";
//        GUI instance = null;
//        instance.updateMembersList(list);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of startPopup method, of class GUI.
     */
    @Test
    public void testStartPopup() {
        
        String usernameText = "Henk";
        StringSelection stringSelection = new StringSelection(usernameText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);

        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_H);
            robot.keyRelease(KeyEvent.VK_H);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyPress(13);
            robot.keyRelease(13);
        } catch (AWTException ex) {
            Logger.getLogger(GUITest.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("startPopup");
        String[] expResult = new String[]{usernameText, "", "59001"};
        String[] result = instance.startPopup();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

}
