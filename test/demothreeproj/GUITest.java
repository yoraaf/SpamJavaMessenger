package demothreeproj;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author SPAM
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
        boolean arg = false;
        boolean result =instance.setAllowedToMsg(arg);
        Assert.assertEquals(arg, result);
        System.out.println("testSetAllowedToMsg: exp "+arg+" res "+result);
    }

    /**
     * Test of setTitle method, of class GUI.
     */
    @Test
    public void testSetTitle() {
        String title = "This Is A Title";
        String result = instance.setTitle(title);
        Assert.assertEquals(title, result);
        System.out.println("testSetTitle: exp "+title+" res "+result);
    }

    /**
     * Test of updateMembersList method, of class GUI.
     */
    @Test
    public void testUpdateMembersList() {
        String list = "Henk    172.0.0.1    59001";
        String result = instance.updateMembersList(list);
        Assert.assertEquals(result, list);
        System.out.println("testUpdateMembersList: exp "+list+" res "+result);
    }

}
