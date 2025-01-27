package src;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main {

    private static final String TRACKER_CONNECT_COMMAND = "connect tracker";
    private static final String TRACKER_DISCONNECT_COMMAND = "disconnect tracker";
    
    private static final String HELP_COMMAND = "help";
    private static final String EXIT_COMMAND = "exit";

    private static final String CONNECT_COMMAND = "connect";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String SEND_COMMAND = "send";
    private static final String LOAD_FILE_COMMAND = "load";
    private static final String REMOVE_FILE_COMMAND = "remove";
    private static final String LIST_COMMAND = "list";
    private static final String NEW_FILE_COMMAND = "new";

    private static ConfigReader config = new ConfigReader();

    private static InetAddress TRACKER_ADDRESS;
    private static int TRACKER_PORT;
    private static int REFRESH_RATE;
    private static Peer peer;
    private static int port;

    private static void init(){
        config.Parse();
        TRACKER_ADDRESS = config.TrackerAddress;
        TRACKER_PORT = config.TrackerPort;
        REFRESH_RATE = config.RefreshRate;
        System.out.println("Parsed");
    }

    private static void handleConnect(String[] tokens) throws IOException, UnknownHostException {

        
        int nb_peer = (tokens.length);
        for (int i = 1; i < nb_peer; i++) {
            System.out.println("****************************************************");
            if (tokens[i].equals("tracker")) {                
                if (!peer.haveCommunication(TRACKER_ADDRESS, TRACKER_PORT)) {
                    System.out.println("Connecting to tracker ...");
                    peer.connect(TRACKER_ADDRESS, TRACKER_PORT);
                    System.out.println("Connection successful\n");
                    String[] announce = {"send", "<announce", "listen", "" + port, "seed", "[", peer.getFiles(), "]>", "tracker"};
                    handleSend(announce);
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

        // Concatenate message parts separated by space
        StringBuilder messageBuilder = new StringBuilder();
        int i = 1;

        for (i = 1; i < tokens.length; i++) {
            if (tokens[i].startsWith("<")) {
                tokens[i] = tokens[i].substring(1);
            }
            
            messageBuilder.append(tokens[i]).append(" ");

            if (tokens[i].endsWith(">")) {
                messageBuilder.deleteCharAt(messageBuilder.length() - 2);
                break;
            }
        }

        String message = messageBuilder.toString();
        System.out.println("< " + message + "\n");

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
        System.out.println("To connect to tracker                   " + TRACKER_CONNECT_COMMAND + "\n");
        System.out.println("To disconnect from tracker              " + TRACKER_DISCONNECT_COMMAND + "\n");
        System.out.println("To connect to peer                      " + CONNECT_COMMAND + " $ip1:$port1 $ip2:$port2 ...\n");
        System.out.println("To disconnect to peer                   " + DISCONNECT_COMMAND + " $ip1:$port1 $ip2:$port2 ...\n");
        System.out.println("To send message to peer                 " + SEND_COMMAND + " <$message> $ip1:$port1 $ip2:$port2 ...\n");
        System.out.println("To exit the client                      " + EXIT_COMMAND + "\n");
        System.out.println("To create a new file                    " + NEW_FILE_COMMAND +  " file $file_name $piece_size $file_size\n");
        System.out.println("To load a file to the peer storage      " + LOAD_FILE_COMMAND +  " file $path_to_file $piece_size\n");
        System.out.println("To remove a file to the peer storage    " + REMOVE_FILE_COMMAND +  " file $path_to_file\n");
        System.out.println("To list peers connected to the peer     list peers\n");
        System.out.println("To list files in peer storage           list files\n");
        System.out.println("To list bitmap in peer storage          list bitmap\n");
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

                case LOAD_FILE_COMMAND:
                    if (tokens.length < 3){
                        usage();
                    }

                    else if (tokens[1].equals("file")){
                        if (tokens.length == 3){
                            peer.loadFile(tokens[2]);
                        }
                        else { 
                            peer.loadFile(tokens[2], Integer.parseInt(tokens[3]));
                        }
                    }
                    break;

                case NEW_FILE_COMMAND:
                    if (tokens.length < 5){
                        usage();
                        break;
                    }
                    else if (tokens[1].equals("file")){
                        peer.newFile("data/" + tokens[2], Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]));
                        break;
                    }
                    usage();
                    break;

                case REMOVE_FILE_COMMAND:
                    if (tokens.length < 3){
                        usage();
                    }
                    
                    else if (tokens[1].equals("file")) {
                        peer.removeFile(tokens[2]);
                    }
                    break;
                
                case LIST_COMMAND:
                    if (tokens[1].equals("files")){
                        peer.listFiles();
                    }
                    
                    else if (tokens[1].equals("bitmap")){
                        peer.listBitMap();
                    }

                    else if (tokens[1].equals("peers")){
                        peer.displayPeers();
                    }

                    else {
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

        String ip = "127.0.0.1"; // Adresse par défaut
        port = 5050; // Port par défaut
        boolean debug = false;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                debug = args.length > 1 && Integer.parseInt(args[1]) == 1;
            } catch (NumberFormatException e) {
                System.err.println("Le port spécifié n'est pas un nombre valide. Utilisation du port par défaut : 5050");
            }
        }

        peer = new Peer(ip, port, TRACKER_PORT, TRACKER_ADDRESS);

        if (debug) {
             try {Thread.sleep(2000);} catch (InterruptedException e) {
                e.printStackTrace();
             }
            int[] ports = {5000, 5001, 5002};
            System.out.println("Debug mode enabled\n");
            peer.connect(TRACKER_ADDRESS, TRACKER_PORT);
            for (int p : ports)
                if (p == port)
                    continue;
                else
                    peer.connect(InetAddress.getByName("localhost"), p);
        }

        System.out.println("Type message to send ('help' to get details, 'exit' to quit):");

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(peer::informState, 0, REFRESH_RATE, TimeUnit.SECONDS);

        while (true) {
            String newCommand = readInput();
            handleUserCommand(newCommand);
        }
    }

}

