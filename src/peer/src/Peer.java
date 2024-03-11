import java.io.IOException;
import java.net.*;

public class Peer {

    private int portNumber;
    private int trackerPortNumber;
    private InetAddress trackerIpAdress;
    private InetAddress IpAdress;
    private File[] files;
    
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
            // Connexion réussie, vous pouvez effectuer d'autres opérations ici si nécessaire
        } catch (IOException e) {
            // Gestion de l'exception en lançant une IOException
            System.out.println("I/O error: " + e.getMessage());
        }
    }

    public void connectToTracker() throws IOException {
        try (Socket socket = new Socket(trackerIpAdress, trackerPortNumber)){
            System.out.println("Connexion au tracker réussie\n");
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }
    
    public void init() {
        //charger fichier config;
    }
}