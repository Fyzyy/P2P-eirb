package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionException;
import src.Parser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NonBlockingServerWithThread {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 8080));
        serverSocketChannel.configureBlocking(false); // Configurer le canal du serveur en mode non bloquant

        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // Enregistrer le canal du serveur pour les
                                                                        // événements d'acceptation

        ExecutorService executorService = Executors.newFixedThreadPool(10); // Créer un pool de threads

        while (true) {
            selector.select(); // Attendre qu'un événement se produise

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();

                if (key.isAcceptable()) {
                    // Accepter une nouvelle connexion
                    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = serverChannel.accept();
                    clientChannel.configureBlocking(false); // Configurer le canal du client en mode non bloquant
                    clientChannel.register(selector, SelectionKey.OP_READ); // Enregistrer le canal du client pour les
                                                                            // événements de lecture
                } else if (key.isReadable()) {
                    // Lire les données envoyées par le client dans un thread séparé du pool
                    executorService.submit(() -> {
                        try {
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int bytesRead = clientChannel.read(buffer);
                            if (bytesRead == -1) {
                                // La connexion a été fermée par le client
                                clientChannel.close();
                            } else if (bytesRead > 0) {
                                buffer.flip();
                                byte[] bytes = new byte[buffer.remaining()];
                                buffer.get(bytes);
                                String message = new String(bytes);
                                System.out.println("Received: " + message);

                                // Traiter le message reçu dans le thread du pool, par exemple, parser le
                                // message
                                parseMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    private static void parseMessage(String message) {
        // Ajoutez ici la logique de traitement du message
        System.out.println("Parsing message: " + message);
    }
}

public class Listener extends Thread {

    private int portNumber;
    private Socket tmpSocket;
    private ServerSocket serverSocket;
    private InputStream iStream;
    private BufferedReader pReader;
    private boolean exit = false;
    private ExecutorService executor;
    private List<Communication> communications;

    public Listener(int portNumber) throws IOException {
        this.portNumber = portNumber;
        this.executor = new ThreadPoolExecutor(10, 50, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
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
                communications.add(new Communication(tmpSocket));
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        handleConnection(tmpSocket);
                    }
                });
                System.out.println("Extern Peer connected");
            } catch (IOException e) {
                // Handle exception
            } catch (RejectedExecutionException e) {
                System.out.println("Too much connections, sent to queue\n");
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
                System.out.println("Received: " + s);
                Parser.parseCommand(s);
            }
            System.out.println("Connection closed");
            socket.close();
        } catch (IOException e) {
            System.out.println("unable to receive message \n");
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