package tst;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

import src.Peer;

public class TestPeer {

    @Test
    void testConnect() throws IOException {
        Peer p = new Peer(8080);
        p.connect(InetAddress.getByName("127.0.0.1"), 5050);
        assertTrue(p.haveCommunication(InetAddress.getByName("127.0.0.1"), 5050));
        assertFalse(p.haveCommunication(InetAddress.getByName("127.0.0.2"), 5050));
    }

    @Test
    void testDisconnect() throws IOException {
        Peer p = new Peer(8080);
        p.connect(InetAddress.getByName("127.0.0.1"), 5050);
        assertTrue(p.haveCommunication(InetAddress.getByName("127.0.0.1"), 5050));
        p.disconnect(InetAddress.getByName("127.0.0.1"), 5050);
        assertFalse(p.haveCommunication(InetAddress.getByName("127.0.0.1"), 5050));
    }
    
}
