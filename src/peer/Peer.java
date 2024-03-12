package peer;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class Peer {

    private int portNumber;
    private int trackerPortNumber;
    private InetAddress trackerIpAdress;
    private InetAddress IpAdress;
    private SharedFile[] files;
    private Socket socket;
    
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
        }
    }

    public void connectToTracker() throws IOException {
        try (Socket socket = new Socket(trackerIpAdress, trackerPortNumber)){
            System.out.println("Connexion au tracker réussie\n");
            this.socket = socket;
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
            throw e;
        }
        System.out.println(this.socket);
    }

    public void sendMessage(String message) throws IOException{
        try {
            DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
            try {
                dos.writeUTF(message);   
                dos.flush();    
            } catch (Exception e) {
                System.out.println("Cannot send message");
            }
            dos.close();
        } catch (Exception e) {
            System.out.println("Cannot create outpout stream");
        }
    }

    public void printSocket() {
        System.out.println(this.socket);
    }

    public void endConnection() throws IOException{
        this.socket.close();
    }
    
    public void init() {
        //charger fichier config;
    }
}