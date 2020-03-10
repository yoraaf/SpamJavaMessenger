/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demothreeproj;



import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author rv1658f
 */
public class RedirectServerTest {

    public RedirectServerTest() {
    }
    RedirectServer serverInstance;
    Socket socket;
    Scanner in;
    PrintWriter out;
    String ipToTest = "127.0.0.1";

    @Before
    public void setUp() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    serverInstance = new RedirectServer(ipToTest, 59001);
                } catch (Exception ex) {
                    Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
        try {
            socket = new Socket("127.0.0.1", 59001);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception ex) {
            Logger.getLogger(RedirectServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of closeServer method, of class RedirectServer.
     */
    @Test
    public void testResponse() {
        String line = in.nextLine();
        assertEquals(line, "REDIRECT "+ipToTest);
        System.out.println(line);

    }

}
