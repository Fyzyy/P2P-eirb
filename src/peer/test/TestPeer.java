package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

import peer.Peer;


public class TestPeer {

    @Test
    void testErreurConnexion() throws UnknownHostException {
        Peer p = new Peer(InetAddress.getByName("127.0.0.2"), 7, InetAddress.getByName("127.14.21.12"), 493);
        try {
            p.connectToTracker();
            assertTrue(false);
        } catch (IOException e) {
            assertTrue(true);
        }  
    }
    
}
