package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import src.Parser;

public class Listener extends Thread {

    private int portNumber;
    private Socket tmpSocket;
    private ServerSocket serverSocket;
    private InputStream iStream;
    private BufferedReader pReader;
    private boolean exit = false;
    private ExecutorService executor;

    public Listener(int portNumber) throws IOException {
        this.portNumber = portNumber;
        this.executor = new ThreadPoolExecutor(10, 50, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));
    }
    
    public void run() {
        System.out.println("Start Listening for Peers...");
        try {
            serverSocket = new ServerSocket(this.portNumber);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        while (!exit) {
            try {
                final Socket tmpSocket = this.serverSocket.accept();
                System.out.println("Extern Peer connected");
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        handleConnection(tmpSocket);
                    }
                });
            } catch (IOException e) {
                // Handle exception
            }
        }
        executor.shutdown(); // Arrête la pool de threads
        System.out.println("The END");
    }

    private void handleConnection(Socket socket) {
        try {
            String s = "";
            BufferedReader pReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((s = pReader.readLine()) != null) {
                Parser.parsePeerCommand(s);
                System.out.println(s);
            }
            System.out.println("Connection closed");
            socket.close();
        } catch (IOException e) {
            // Handle exception
        }
    }

    public void endListening() throws IOException {
        exit = true;
        if (this.serverSocket != null) {
            this.serverSocket.close();
        }
        System.out.println("End Listening");
    }

}
