import java.io.IOException;
import java.net.InetAddress;

public class Main {
    static public void main(String argv[]) throws IOException{

        // Pour créer un InetAdress, on effectue comme suit
        InetAddress addrPeer = InetAddress.getByName("127.0.0.2");
        InetAddress addrTracker = InetAddress.getByName("127.0.0.1");

        // Création d'une Peer
        Peer peer = new Peer(addrPeer, 8081, addrTracker, 8080);
        
        // Connexion au Tracker
        peer.connectToTracker();
    }
}
