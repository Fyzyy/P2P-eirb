package peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread {

    private int portNumber;
    private Socket serverSocket;
    private InputStream iStream;
    private BufferedReader pReader;
    private boolean exit = false;

    public Listener(int portNumber) throws IOException {
        this.portNumber = portNumber;
    }

    public void startListening() throws IOException {
        System.out.println("coucou");
        ServerSocket tmpSocket = new ServerSocket(this.portNumber);
        this.serverSocket = tmpSocket.accept(); // --> Bloque ICI
        this.iStream = this.serverSocket.getInputStream();
        this.pReader = new BufferedReader(new InputStreamReader(this.iStream));
        tmpSocket.close();
    }
    
    public void run() {
        try {
            this.startListening();
            while (!exit) {
                String s = this.pReader.readLine();
                System.out.println(s);
                // TODO parsing
            }
            this.endListening();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
    }

    public void endListening() throws IOException {
        exit = true;
        if (serverSocket != null && iStream != null && pReader != null){    
            this.serverSocket.close();
            this.iStream.close();
            this.pReader.close();
        }
    }

}
