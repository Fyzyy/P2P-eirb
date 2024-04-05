package src;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class Main {

    private static final String TRACKER_CONNECT_COMMAND = "connect tracker";
    private static final String TRACKER_DISCONNECT_COMMAND = "disconnect tracker";
    
    private static final String HELP_COMMAND = "help";
    private static final String EXIT_COMMAND = "exit";

    private static final String CONNECT_COMMAND = "connect peer";
    private static final String DISCONNECT_COMMAND = "disconnect peer";


    private static Peer peer;

    private static void connectToTracker() throws IOException {
        peer.connectToTracker();
        peer.setConnexionToTrackerStatus(true);
    }

    private static void connectToPeer(InetAddress address, int port) throws IOException {
        peer.connectToPeer(address, port);
    }

    private static void disconnectFromTracker() throws IOException {
        peer.endTrackerConnection();
        peer.setConnexionToTrackerStatus(false);
        System.out.println("Disconnection successful\n");
    }

    private static void sendMessageToTracker(String message) {
        try {
            peer.sendMessage(message);
        } catch (Exception e) {
            System.out.println("***** Disconnection *****\n");
            peer.setConnexionToTrackerStatus(false);
        }
    }

    private static void handleUserCommand(String command) throws IOException {
        if (command.equalsIgnoreCase(EXIT_COMMAND)) {
            if (peer.getConnexionToTrackerStatus() == true) {
                peer.exit();
            } else {
                peer.endListener();
            }
            System.exit(0);
        } else if (command.equalsIgnoreCase(TRACKER_CONNECT_COMMAND) || command.equalsIgnoreCase("ct")) {
            if (peer.getConnexionToTrackerStatus() == false) {
                connectToTracker();
            } else {
                System.out.println("You are already connected\n");
            }
        } else if (command.equalsIgnoreCase(TRACKER_DISCONNECT_COMMAND)) {
            if (peer.getConnexionToTrackerStatus() == true) {
                disconnectFromTracker();
            }
        } else if (command.equalsIgnoreCase(CONNECT_COMMAND)) {
            System.out.println("Usage: IpAdress PortNumber");
            String answer = readInput();
            String answers[] = answer.split(" ");
            if (answers.length == 2){
                connectToPeer(InetAddress.getByName(answers[0]), Integer.parseInt(answers[1])); 
                peer.setConnexionToPeerStatus(true);            
            }
            else{
                System.out.println("Error missing arguments, connexion to peer process stoped.");
            }
        } else if (command.equalsIgnoreCase(DISCONNECT_COMMAND)) {
            if(peer.getConnexionToPeerStatus()==true){
                peer.endPeerConnection();
                peer.setConnexionToPeerStatus(false);
                System.out.println("Deconnection to peer\n");
            }
            else{
                System.out.println("Connect to peer before deconnection");
            }
        } else if (command.equalsIgnoreCase(HELP_COMMAND)) {
            System.out.println("**************************************************************");
            System.out.println("To connect to tracker, type: " + TRACKER_CONNECT_COMMAND + "\n");
            System.out.println("To disconnect from tracker, type: " + TRACKER_DISCONNECT_COMMAND + "\n");
            System.out.println("To connect to peer, type: " + CONNECT_COMMAND + "\n");
            System.out.println("To disconnect to peer, type: " + DISCONNECT_COMMAND + "\n");
            System.out.println("To exit the client, type: " + EXIT_COMMAND);
            System.out.println("**************************************************************");
        }  else {
            if (peer.getConnexionToTrackerStatus() == true) {
                sendMessageToTracker(command);
            } else {
                System.out.println("Please, connect before sending messages\n");
            }
        }
    }

    private static String readInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) throws IOException {

        int port = 5050; // Port par défaut
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Le port spécifié n'est pas un nombre valide. Utilisation du port par défaut : 5050");
            }
        }

        InetAddress addrPeer = InetAddress.getByName("127.0.0.2");
        InetAddress addrTracker = InetAddress.getByName("127.0.0.1");

        peer = new Peer(addrPeer, port, addrTracker, 8080);

        System.out.println("Type message to send ('help' to get details, 'exit' to quit):");

        while (true) {
            String newCommand = readInput();
            handleUserCommand(newCommand);
        }
    }
}
