package peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Listener extends Thread {

    private int portNumber;
    private ServerSocket serverSocket;
    private InputStream iStream;
    private BufferedReader pReader;
    private boolean exit = false;

    public Listener(int portNumber) throws IOException {
        this.portNumber = portNumber;
    }
    
    public void run() {
        try {
            serverSocket = new ServerSocket(this.portNumber);
            serverSocket.setSoTimeout(1000);
            System.out.println("Start Listening for Peers...");
            while (!exit) {
                try {
                    Socket tmpSocket = this.serverSocket.accept();
                    System.out.println("coucou");
                    this.iStream = tmpSocket.getInputStream();
                    this.pReader = new BufferedReader(new InputStreamReader(this.iStream));
                    String s = this.pReader.readLine();
                    System.out.println(s);
                    // TODO parsing
                    tmpSocket.close();
                } catch (SocketTimeoutException e) {
                    // Exception lancée lorsqu'aucune connexion entrante n'est reçue dans le délai spécifié
                    // Cela permet de vérifier périodiquement si exit est toujours false
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
    }

    public void endListening() throws IOException {
        exit = true;
        System.out.println("End Listening");
    }

}
