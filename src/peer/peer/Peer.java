package peer;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Peer {

    private int portNumber;
    private int trackerPortNumber;
    private InetAddress IpAdress;
    private InetAddress trackerIpAdress;
    private SharedFile[] files;
    private Socket socket;
    private DataOutputStream sender;
    private BufferedReader reader;
    private int connectedToTracker = 0;
    
    //fichiers disponibles (Hashmap ?)

    public Peer(InetAddress IpAddress, int portNumber, InetAddress trackerIpAddress, int trackerPortNumber) {
        this.trackerIpAdress = trackerIpAddress;
        this.portNumber = portNumber;
        this.IpAdress = IpAddress;
        this.trackerPortNumber = trackerPortNumber;
    }

    // Utilisation d'une méthode séparée pour établir la connexion avec le tracker
    public void connectToPeer(InetAddress PeerAdress, int PeerPortNumber) throws IOException {
        try (Socket socket = new Socket(PeerAdress, PeerPortNumber)){
            System.out.println("Connexion réussie\n");
            this.socket = socket;
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
            throw e;
        }
    }

    public void connectToTracker() throws IOException {
        try {
            socket = new Socket(trackerIpAdress, trackerPortNumber);
            System.out.println("Connection to tracker succesful\n");
            sender = new DataOutputStream(this.socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {   
            System.out.println("I/O error: " + e.getMessage());
            throw e;
        }
    }
    
    public void sendMessage(String message) throws IOException{
        byte[] bytes = message.getBytes("UTF-8");
        try {
            sender.write(bytes);
            sender.flush();
            try {
                System.out.println(reader.readLine());
            } catch (IOException e) {
                throw e;
            }
        } catch (IOException e) {
            System.out.println("Cannot send message");
            throw e;
        }
    }

    public void printSocket() {
        System.out.println(this.socket);
    }

    public void endConnection() throws IOException{
        sender.close();
        this.socket.close();
    }
    
    public void init() {
        //charger fichier config;
    }

    public int getConnexionToTrackerStatus(){
        return this.connectedToTracker;
    }

    public void setConnexionToTrackerStatus(int status){
        this.connectedToTracker = status;
    }
}