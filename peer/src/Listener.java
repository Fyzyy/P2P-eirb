package src;

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

public class Listener extends Thread {

    private int portNumber;
    private Selector selector;
    private ExecutorService messageHandlerPool;

    public Listener(int portNumber) {
        this.portNumber = portNumber;
        this.messageHandlerPool = Executors.newFixedThreadPool(10); // Pool de threads pour le traitement des messages
    }

    public void run() {
        try {
            // Ouvrir le canal du serveur et le configurer en mode non bloquant
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            // Liaison du canal du serveur au port spécifié
            serverSocketChannel.socket().bind(new InetSocketAddress(portNumber));

            // Ouvrir le sélecteur et l'enregistrer avec le canal du serveur pour les connexions entrantes
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Start Listening for Peers...");

            // Boucle principale pour écouter les événements
            while (true) {
                // Sélection des canaux prêts pour l'opération
                int readyChannels = selector.select();

                // Si aucun canal n'est prêt, passer à l'itération suivante
                if (readyChannels == 0) {
                    continue;
                }

                // Obtenir l'ensemble des clés sélectionnées
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                // Parcourir les clés sélectionnées et gérer les événements
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) {
                        handleAcceptableEvent(serverSocketChannel, key);
                    } else if (key.isReadable()) {
                        handleReadableEvent(key);
                    }

                    // Supprimer la clé traitée pour éviter de la traiter à nouveau
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            System.out.println("Error while listening for connections: " + e.getMessage());
        }
    }

    private void handleAcceptableEvent(ServerSocketChannel serverSocketChannel, SelectionKey key) throws IOException {
        // Accepter la connexion entrante
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        // Enregistrer le canal avec le sélecteur pour les lectures futures
        socketChannel.register(selector, SelectionKey.OP_READ);

        System.out.println("Extern Peer connected: " + socketChannel.getRemoteAddress());
    }

    private void handleReadableEvent(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            // Lire les données du canal dans le tampon
            int bytesRead = socketChannel.read(buffer);
            if (bytesRead == -1) {
                // La connexion a été fermée par le client
                System.out.println("Connection closed: " + socketChannel.getRemoteAddress());
                socketChannel.close();
                return;
            }

            // Convertir les données lues du tampon en une chaîne de caractères
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String message = new String(bytes);

            // Envoyer le message pour traitement dans le pool de threads
            messageHandlerPool.execute(() -> handleIncomingMessage(message));

            System.out.println("Received: " + message + " from " + socketChannel.getRemoteAddress());
        } catch (IOException e) {
            System.out.println("Error while reading message: " + e.getMessage());
            try {
                socketChannel.close();
            } catch (IOException ex) {
                // Ignorer l'exception lors de la fermeture du canal
            }
        }
    }

    private void handleIncomingMessage(String message) {
        // Effectuer le traitement du message ici
        // Exemple : Parser.parseCommand(message);
    }

    public void endListening() {
        // Arrêter le pool de threads
        messageHandlerPool.shutdown();
        System.out.println("The END");
    }
}
