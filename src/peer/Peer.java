package peer;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class Peer {

    private int portNumber;
    private int trackerPortNumber;
    private InetAddress IpAdress;
    private InetAddress trackerIpAdress;
    private SharedFile[] files;
    Socket socket;
    private DataOutputStream dos;
    
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
            // Connexion réussie, vous pouvez effectuer d'autres opérations ici si nécessaire
        } catch (IOException e) {
            // Gestion de l'exception en lançant une IOException
            System.out.println("I/O error: " + e.getMessage());
            throw e;
        }
    }

    public void connectToTracker() throws IOException {
        try {
            socket = new Socket(trackerIpAdress, trackerPortNumber);
            System.out.println("Connexion au tracker réussie\n");
            dos = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {   
            System.out.println("I/O error: " + e.getMessage());
            throw e;
        }
    }

    public void sendMessage(String message) throws IOException{
        try {
            dos.writeUTF(message);  
            dos.flush();    
        } catch (IOException e) {
            System.out.println("Cannot send message");
            throw e;
        }
    }

    public void printSocket() {
        System.out.println(this.socket);
    }

    public void endConnection() throws IOException{
        dos.close();
        this.socket.close();
    }
    
    public void init() {
        //charger fichier config;
    }
}