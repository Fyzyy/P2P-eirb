package src;
import java.io.DataOutputStream;
import java.io.BufferedReader;
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
    private BufferedReader tReader;
    private boolean connectedToTracker = false;
    private boolean connectedToPeer = false;
    private Listener listener;
    
    //fichiers disponibles (Hashmap ?)

    public Peer(InetAddress IpAddress, int portNumber, InetAddress trackerIpAddress, int trackerPortNumber) throws IOException {
        this.trackerIpAdress = trackerIpAddress;
        this.IpAdress = IpAddress;
        this.trackerPortNumber = trackerPortNumber;
        this.portNumber = portNumber;
        listener = new Listener(this.portNumber);
        this.listener.start();
    }

    // Utilisation d'une méthode séparée pour établir la connexion avec le tracker
    public void connectToPeer(InetAddress PeerAdress, int PeerPortNumber) throws IOException {
        try (Socket socket = new Socket(PeerAdress, PeerPortNumber)){
            String adress = PeerAdress.toString();
            peerManager.connectPeer(adress, PeerPortNumber);
            System.out.println("Connexion réussie\n");
            this.sender = new DataOutputStream(this.socket.getOutputStream());
            this.tReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
            throw e;
        }
    }

    public boolean isConnectedToPeer(InetAddress PeerAdress, int PeerPortNumber) {
        String adress = PeerAdress.toString();
        return peerManager.isConnected(adress, PeerPortNumber);
    }

    public void disconnectPeer(InetAddress PeerAdress, int PeerPortNumber) {
        String adress = PeerAdress.toString();
        peerManager.disconnectPeer(adress, PeerPortNumber);
    }

    public void connectToTracker() throws IOException {
        try {
            this.socket = new Socket(trackerIpAdress, trackerPortNumber);
            System.out.println("Connection to tracker succesful\n");
            this.sender = new DataOutputStream(this.socket.getOutputStream());
            this.tReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
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
                System.out.println(tReader.readLine());
            } catch (IOException e) {
                System.out.println("Cannot read message");
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

    public void endTrackerConnection() throws IOException{
        this.sender.close();
        this.socket.close();
        this.tReader.close();
    }

    public void endPeerConnection() throws IOException{
        this.sender.close();
        this.socket.close();
        this.tReader.close();
    }

    public void endListener() throws IOException{
        listener.endListening();
    }

    public void exit() throws IOException{
        endListener();
        endTrackerConnection();
    }
    
    public void init() {
        //charger fichier config;
    }

    public boolean getConnexionToTrackerStatus(){
        return this.connectedToTracker;
    }

    public void setConnexionToTrackerStatus(boolean status){
        this.connectedToTracker = status;
    }

    public boolean getConnexionToPeerStatus(){
        return this.connectedToPeer;
    }

    public void setConnexionToPeerStatus(boolean status){
        this.connectedToPeer = status;
    }
}