import java.io.IOException;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    private static String readInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    static public void main(String argv[]) throws IOException{

        
        // Pour créer un InetAdress, on effectue comme suit
        InetAddress addrPeer = InetAddress.getByName("127.0.0.2");
        InetAddress addrTracker = InetAddress.getByName("127.0.0.1");
        
        // Peer creation
        Peer peer = new Peer(addrPeer, 8081, addrTracker, 8080);
        
        // Connection to Tracker
        try {
            peer.connectToTracker();
        } catch (IOException e) {
            return;
        }

        while(true){
            System.out.println("Entrez un message à envoyer (ou 'exit' pour quitter) :");
            String newCommand = readInput();

            // Vérifie si l'utilisateur souhaite quitter
            if (newCommand.equalsIgnoreCase("exit")) {
                peer.endConnection();
                break;
            }
            
            peer.sendMessage(newCommand);

        }

    }
}
