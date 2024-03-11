import java.io.IOException;
import java.net.*;

public class Peer {

    private int portNumber;
    private int trackerPortNumber;
    private InetAddress trackerIpAdress;
    private InetAddress IpAdress;
    private Socket socket;
    private File[] files;
    
    //fichiers disponibles (Hashmap ?)

    public Peer(InetAddress IpAddress, int portNumber, InetAddress trackerIpAddress, int trackerPortNumber) {
        this.trackerIpAdress = trackerIpAddress;
        this.portNumber = portNumber;
        this.IpAdress = IpAddress;
        this.trackerPortNumber = trackerPortNumber;
    }

    // Utilisation d'une méthode séparée pour établir la connexion avec le tracker
    public void connectToTracker() throws IOException {
        try {
            socket = new Socket(IpAdress, portNumber);
            // Connexion réussie, vous pouvez effectuer d'autres opérations ici si nécessaire
        } catch (Exception e) {
            // Gestion de l'exception en lançant une IOException
            throw new IOException("Erreur lors de la connexion au tracker.", e);
        }
    }

    public void init() {
        //charger fichier config;
    }

}