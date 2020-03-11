/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demothreeproj;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
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

}
