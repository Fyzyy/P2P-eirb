import java.io.IOException;
import java.net.InetAddress;

public class Main {
    static public void main(String argv[]) throws IOException{
        InetAddress addrPeer = InetAddress.getByName("127.0.0.2");
        InetAddress addrTracker = InetAddress.getByName("127.0.0.1");
        Peer peer = new Peer(addrPeer, 8081, addrTracker, 8080);
        peer.connectToTracker();
    }
}
