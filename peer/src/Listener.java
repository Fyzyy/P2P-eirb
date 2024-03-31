package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import src.Parser;

public class Listener extends Thread {

    private int portNumber;
    private Socket tmpSocket;
    private ServerSocket serverSocket;
    private InputStream iStream;
    private BufferedReader pReader;
    private boolean exit = false;

    public Listener(int portNumber) throws IOException {
        this.portNumber = portNumber;
    }
    
    public void run() {
        System.out.println("Start Listening for Peers...");
        try{
            serverSocket = new ServerSocket(this.portNumber);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        while (!exit) {
            try {
                serverSocket.setSoTimeout(2000);
                this.tmpSocket = this.serverSocket.accept();
                System.out.println("Extern Peer connected");
                String s = "";
                while (s!=null) {
                    this.iStream = tmpSocket.getInputStream();
                    this.pReader = new BufferedReader(new InputStreamReader(this.iStream));
                    s = this.pReader.readLine();
                    if (s == null){
                        break;
                    }
                    System.out.println(s);
                }

                Parser.parsePeerCommand(s);
                            
                System.out.println("Connection closed");
                tmpSocket.close();
            }
            catch (IOException e) {
                // System.out.println(e.getMessage());
            }
        } 
        System.out.println("The END");
    }

    public void endListening() throws IOException {
        exit = true;
        if (this.tmpSocket != null) {
            this.tmpSocket.close();
        }
        this.serverSocket.close();
        System.out.println("End Listening");
    }

}
