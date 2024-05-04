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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Listener extends Thread {

    private String ip;
    private int portNumber;
    private Selector selector;
    private ExecutorService messageHandlerPool;
    private Parser parser;
    private int haveSendMessage = 0;

    public Listener(String ip, int portNumber, Parser parser) {
        this.portNumber = portNumber;
        this.messageHandlerPool = Executors.newFixedThreadPool(10); // Pool de threads pour le traitement des messages
        this.parser = parser;
        this.ip = ip;
    }

    public void run() {
        try {
            int attempt = 0;
            // Ouvrir le canal du serveur et le configurer en mode non bloquant
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            // Liaison du canal du serveur au port spécifié
            serverSocketChannel.socket().bind(new InetSocketAddress(ip, portNumber));
            serverSocketChannel.socket().setReuseAddress(true);
            System.out.println("ip: " + serverSocketChannel.socket().getInetAddress() + " port: "
                    + serverSocketChannel.socket().getLocalPort());

            // Ouvrir le sélecteur et l'enregistrer avec le canal du serveur pour les
            // connexions entrantes
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
                        attempt = handleReadableEvent(key, attempt);
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

    private int handleReadableEvent(SelectionKey key, int attempt) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int newAttempt = attempt;
        
        try {
            // Lire les données du canal dans le tampon
            int bytesRead = socketChannel.read(buffer);
            if (bytesRead == -1 || attempt >= 3) {
                // La connexion a été fermée par le client
                System.out.println("Connection closed: " + socketChannel.getRemoteAddress());
                socketChannel.close();
                return 0;
            }
    
            // Convertir les données lues du tampon en une chaîne de caractères
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            String message = new String(bytes);

            System.out.println("> " + message + " from " + socketChannel.getRemoteAddress());
    
            try {
                Future<Response> responseFuture = messageHandlerPool.submit(() -> handleIncomingMessage(message));
                Response response = responseFuture.get();
                if (response.getMessage()!=null){

                    switch (response.getType()) {
                        
                        case NO_RESPONSE:
                            System.out.println("No response\n");
                            newAttempt++;
                            break;

                        case UNKNOW:
                            if(this.haveSendMessage > 0){
                                System.out.println("Unknown : " + message);
                                newAttempt++;
                            }
                            break;
                        
                        case ERROR:
                            System.out.println("Error while processing message: " + response.getMessage());
                            newAttempt++;
                            break;
                        
                        default:
                            System.out.println("< " + response.getMessage());
                            newAttempt=0;
                            break;
                    }
                    
                }

                else{
                    response.setMessage("Unknown command");
                    response.setType(ResponseType.UNKNOW);
                    newAttempt++;
                }

                if (response.getType() != ResponseType.NO_RESPONSE){
                    // Envoyer la réponse au pair
                    response.send(socketChannel);
                }              

    
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Error while reading message: " + e.getMessage());
            try {
                socketChannel.close();
            } catch (IOException ex) {
            }
        }
        this.haveSendMessage = 0;
        return newAttempt;
    }
    
    private Response handleIncomingMessage(String message) {
        Response response = new Response();
        try {
            response = parser.parseCommand(message);
        } catch (Exception e) {
            response.setType(ResponseType.ERROR);
            response.setMessage("Error while processing message: " + e.getMessage());
        }
        return response;

    }

    public void setHaveSendMessage(){
        this.haveSendMessage = 1;
    }

    public int getHaveSendMessage(){
        return this.haveSendMessage;
    }

    public void endListening() {
        // Arrêter le pool de threads
        messageHandlerPool.shutdown();
        System.out.println("The END");
    }
}
