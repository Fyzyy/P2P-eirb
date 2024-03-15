import java.io.IOException;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import peer.Peer;
import peer.SharedFile;

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
        
        int CONNECTED = 0;
        
        // Pour créer un InetAdress, on effectue comme suit
        InetAddress addrPeer = InetAddress.getByName("127.0.0.2");
        InetAddress addrTracker = InetAddress.getByName("127.0.0.1");
        
        // Peer creation
        Peer peer = new Peer(addrPeer, 8080, addrTracker, 8080);
        System.out.println("Type  message to send ('help' to get details, 'exit' to quit) :");
        
        while(true){
            String newCommand = readInput();
            
            // Vérifie si l'utilisateur souhaite quitter
            if (newCommand.equalsIgnoreCase("exit")) {
                peer.endConnection();
                break;
            }
            
            else if (newCommand.equalsIgnoreCase("tracker connect") && CONNECTED==0){
                // Connection to Tracker
                try {
                    peer.connectToTracker();
                    CONNECTED = 1;
                } catch (IOException e) {
                    return;
                }
            }

            else if (newCommand.equalsIgnoreCase("tracker connect") && CONNECTED==1){
                System.out.println("You are already connected\n");
            }
            
            else if (newCommand.equalsIgnoreCase("tracker disconnect")){
                try {
                    peer.endConnection();
                    CONNECTED = 0;
                    System.out.println("Deconnection succesful\n");
                } catch (IOException e) {
                    return;
                }
            }

            else if (newCommand.equalsIgnoreCase("help")){
                System.out.println("To connect to tracker, type tracker connect");
                System.out.println("To disconnect from tracker, type tracker disconnect\n");
            }
            
            else if (CONNECTED == 1){
                try {
                    peer.sendMessage(newCommand);
                } catch (Exception e) {
                    System.out.println("*****  Déconnexion  *****\n");
                    CONNECTED = 0;
                }
            }

            else{
                System.out.println("Please, connect before sending messages\n");
            }

        }

    }
}
