package src;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.print.DocFlavor.STRING;

public class Main {

    private static final String TRACKER_CONNECT_COMMAND = "connect tracker";
    private static final String TRACKER_DISCONNECT_COMMAND = "disconnect tracker";
    
    private static final String HELP_COMMAND = "help";
    private static final String EXIT_COMMAND = "exit";

    private static final String CONNECT_COMMAND = "connect";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String SEND_COMMAND = "send";
    private static final String ADD_FILE_COMMAND = "add";
    private static final String LIST_FILES_COMMAND = "list";

    private static ConfigReader config = new ConfigReader();

    private static InetAddress TRACKER_ADDRESS;
    private static int TRACKER_PORT;
    private static Peer peer;

    private static void init(){
        config.Parse();
        TRACKER_ADDRESS = config.TrackerAddress;
        TRACKER_PORT = config.TrackerPort;
        System.out.println("Parsed");
    }

    private static void handleConnect(String[] tokens) throws IOException, UnknownHostException {

        
        int nb_peer = (tokens.length);
        for (int i = 1; i < nb_peer; i++) {
            System.out.println("****************************************************");
            if (tokens[i].equals("tracker")) {
                InetAddress address = TRACKER_ADDRESS;
                int port = TRACKER_PORT;
                
                if (!peer.haveCommunication(address, port)) {
                    System.out.println("Connecting to tracker ...");
                    peer.connect(address, port);
                    System.out.println("Connection successful\n");
                } else {
                    System.out.println("You are already connected\n");
                }
            }
            else {
                String[] addressParts = tokens[i].split(":");
                if (addressParts.length == 2) {
                    String ipAddress = addressParts[0];
                    InetAddress address = InetAddress.getByName(ipAddress);
                    int port = Integer.parseInt(addressParts[1]);
        
                    if (!peer.haveCommunication(address, port)) {
                        System.out.println("Connecting to " + address + ":" + port + " ...");
                        peer.connect(address, port);
                        System.out.println("Connection successful\n");
                    } else {
                        System.out.println("You are already connected\n");
                    }
                }
            }
        }
    }

    private static void handleDisconnect(String[] tokens) throws IOException, UnknownHostException {
        int nb_peer = (tokens.length);
        for (int i = 1; i < nb_peer; i++) {
            if (tokens[i].equals("tracker")) {
                InetAddress address = TRACKER_ADDRESS;
                int port = TRACKER_PORT;
    
                if (peer.haveCommunication(address, port)) {
                    System.out.println("Disconnecting from tracker ...");
                    peer.disconnect(address, port);
                    System.out.println("Disconnection successful\n");
                } else {
                    System.out.println("You are not connected\n");
                }
            }
            else {
                String[] addressParts = tokens[i].split(":");
                if (addressParts.length == 2) {
                    String ipAddress = addressParts[0];
                    InetAddress address = InetAddress.getByName(ipAddress);
                    int port = Integer.parseInt(addressParts[1]);
        
                    if (peer.haveCommunication(address, port)) {
                        System.out.println("Disconnecting from " + address + ":" + port + " ...");
                        peer.disconnect(address, port);
                        System.out.println("Disconnection successful\n");
                    } else {
                        System.out.println("You are not connected\n");
                    }
                }
            }
        }
    }

    private static void handleSend(String[] tokens) throws IOException, UnknownHostException {

        for (String token : tokens) {
            System.out.println(token);
        }

        // Concatenate message parts separated by space
        StringBuilder messageBuilder = new StringBuilder();
        int i = 1;

        for (i = 1; i < tokens.length; i++) {
            if (tokens[i].startsWith("\"")) {
                tokens[i] = tokens[i].substring(1);
            }
            
            messageBuilder.append(tokens[i]).append(" ");

            if (tokens[i].endsWith("\"")) {
                messageBuilder.deleteCharAt(messageBuilder.length() - 2);
                break;
            }
        }

        String message = messageBuilder.toString();
        System.out.println("Message to send: " + message + "\n");

        // Extract peer addresses and ports from the command
        for (int peerIndex = i + 1; peerIndex < tokens.length; peerIndex++) {

            if (tokens[peerIndex].equals("tracker")) {
                tokens[peerIndex] = TRACKER_ADDRESS.toString().replace("/", "") + ":" + Integer.toString(TRACKER_PORT);
            }
            
            String[] addressParts = tokens[peerIndex].split(":");

            if (addressParts.length == 2) {
                InetAddress address = InetAddress.getByName(addressParts[0]);
                int port = Integer.parseInt(addressParts[1]);
    
                if (peer.haveCommunication(address, port)) {
                    peer.sendMessage(message, address, port);
                } else {
                    System.out.println("You are not connected to " + address.toString() + ":" + Integer.toString(port) + "\n");
                }
            }
        }
    }


    private static void usage() {
        System.out.println("**************************************************************");
        System.out.println("To connect to tracker, type: " + TRACKER_CONNECT_COMMAND + "\n");
        System.out.println("To disconnect from tracker, type: " + TRACKER_DISCONNECT_COMMAND + "\n");
        System.out.println("To connect to peer, type: " + CONNECT_COMMAND + " $ip1:$port1 $ip2:$port2 ...\n");
        System.out.println("To disconnect to peer, type: " + DISCONNECT_COMMAND + " $ip1:$port1 $ip2:$port2 ...\n");
        System.out.println("To send message to peer, type: " + SEND_COMMAND + " \"$message\" $ip1:$port1 $ip2:$port2 ...\n");
        System.out.println("To exit the client, type: " + EXIT_COMMAND + "\n");
        System.out.println("To add a file to the peer storage, type: " + ADD_FILE_COMMAND +  " file $path_to_file\n");
        System.out.println("To list files in peer storage, type: list files");
        System.out.println("**************************************************************");
        }
    
    

    private static void handleUserCommand(String command) throws IOException {
        String[] tokens = command.split("\\s+");

        if (tokens.length >= 2) {
            switch (tokens[0]) {
                case CONNECT_COMMAND:
                    handleConnect(tokens);
                    break;

                case DISCONNECT_COMMAND:
                    handleDisconnect(tokens);
                    break;

                case SEND_COMMAND:
                    handleSend(tokens);
                    break;

                case ADD_FILE_COMMAND:
                    if (tokens.length != 3){
                        usage();
                    }

                    else if (tokens[1].equals("file")){
                        peer.addFile(tokens[2]);
                    }
                    break;

                case LIST_FILES_COMMAND:
                    if (tokens[1].equals("files")){
                        peer.listFiles();
                    }
                    else{
                        usage();
                    }
                    break;

                default:
                    usage();
                    break;
            }
        }
        else if (tokens.length == 1){
            switch (command) {
                case HELP_COMMAND:
                    usage();
                    break;
                case "peers":
                    peer.displayPeers();
                    break;
                case EXIT_COMMAND:
                    System.out.println("Exiting peer...");
                    System.exit(0);
                    break;
                default:
                    break;
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

        init();

        System.out.println(TRACKER_ADDRESS);
        System.out.println(TRACKER_PORT);

        int port = 5050; // Port par défaut
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Le port spécifié n'est pas un nombre valide. Utilisation du port par défaut : 5050");
            }
        }

        peer = new Peer(port);

        System.out.println("Type message to send ('help' to get details, 'exit' to quit):");

        while (true) {
            String newCommand = readInput();
            handleUserCommand(newCommand);
        }
    }

}

