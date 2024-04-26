package src;

import java.io.*;
import java.net.*;
import java.util.HashSet;

public class Peer {

    private HashSet<Communication> communications;
    private FileManager fileManager;
    private Listener listener;
    private Parser parser;
    private LogManager logManager;

    public Peer(String ip, int portNumber) throws IOException {
        
        communications = new HashSet<Communication>();
        fileManager = new FileManager();
        parser = new Parser(fileManager);
        logManager = new LogManager(portNumber);

        listener = new Listener(ip, portNumber, parser);
        listener.start();
        logManager.createLog(fileManager);
    }

    public Boolean haveCommunication(InetAddress peerAddress, int peerPortNumber) {
        for (Communication communication: communications) {
            if (communication.getSocket().getInetAddress().equals(peerAddress) && communication.getSocket().getPort() == peerPortNumber) {
                return true;
            }
        }
        return false;
    }

    public void connect(InetAddress Address, int PortNumber) throws IOException {
        try {
            Communication communication = new Communication(new Socket(Address, PortNumber));
            this.communications.add(communication);
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }

    public void disconnect(InetAddress peerAddress, int peerPortNumber) throws IOException {
        for (Communication communication : communications) {
            if (haveCommunication(peerAddress, peerPortNumber)) {
                this.communications.remove(communication);
                communication.close();
                System.out.println("Connection closed\n");
                return;
            }
        }
        System.out.println("Peer not found\n");
    }

    public void sendMessage(String message, InetAddress peerAddress, int peerPortNumber) {
        for (Communication communication : communications) {
            if (communication.getSocket().getInetAddress().equals(peerAddress) && communication.getSocket().getPort() == peerPortNumber) {
                //Send
                try {
                    communication.sendMessage(message);

                    ResponseListener responseListener = new ResponseListener(communication, parser);
                    Thread listenerThread = new Thread(responseListener);
                    listenerThread.start();
                } catch (IOException e) {
                    System.out.println("I/O error: " + e.getMessage());
                    try {
                        System.out.println("Fermeture de la communication...");
                        this.disconnect(peerAddress, peerPortNumber);
                    }
                    catch (IOException ex) {
                        System.out.println("I/O error: " + ex.getMessage());
                    }
                }
                return;
            }
        }
        System.out.println("Peer not found\n");
    }

    public String receiveMessage(InetAddress peerAddress, int peerPortNumber) {
        for (Communication communication : communications) {
            if (communication.getSocket().getInetAddress().equals(peerAddress) && communication.getSocket().getPort() == peerPortNumber) {
                //Receive
                try {
                    return communication.receiveMessage();
                } catch (IOException e) {
                    System.out.println("I/O error: " + e.getMessage());
                }
            }
        }
        System.out.println("Peer not found\n");
        return null;
    }

    public void sendFile(String filePath, Communication communication) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            OutputStream os = communication.getSocket().getOutputStream();

            byte[] fileNameBytes = fileManager.getFile(filePath).getFile().getName().getBytes();
            os.write(fileNameBytes);
            os.write('\n');

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1){
                os.write(buffer, 0, bytesRead);
            }

            System.out.println("File sent");
            fis.close();
        } catch (IOException e) {
            System.out.println("Error: Cannot send file");
        }
    }

    public String getFiles(){
        return fileManager.getFiles();
    }

    public void listFiles() {
        System.out.println("Listing files stored in peer storage: ");
        fileManager.listFiles();
    }

    public void listBitMap() {
        System.out.println("Bit map of the peer: ");
        fileManager.getBitMap();
    }

    public void newFile(String filename, int pieceSize, int size) {
        fileManager.createFile(filename, pieceSize, size);
    }

    public void loadFile(String filePath) {
        try {
            System.out.println("Adding file " + filePath + " to peer storage...");
            if(!logManager.checkWordPresenceInLog(filePath)){
                logManager.writeLog(filePath, fileManager);
            }
            fileManager.loadFile(filePath);
            System.out.println("Done");
        } catch (Exception e) {
            System.out.println("Cannot add file to peer storage");
        }
    }

    public void removeFile(String filePath) {
        try {
            System.out.println("Removing " + filePath + " to peer stockage...");
            fileManager.removeFile(filePath);
            logManager.removeFromLog(filePath);
            System.out.println("Done");
        } catch (Exception e) {
            System.out.println("Cannot remove the file");
        }
    }

    public void displayPeers() {
        System.out.println("List of connected peers:");
        for (Communication communication : communications) {
            System.out.println(communication.getSocket().getInetAddress().toString().replace("/", "") + ":" + communication.getSocket().getPort());
        }
    }
}


class ResponseListener implements Runnable {
    private Communication communication;
    private Parser parser;

    public ResponseListener(Communication communication, Parser parser) {
        this.communication = communication;
        this.parser = parser;
    }

    @Override
    public void run() {
        try {
            String response = communication.receiveMessage();
            // if (response.startsWith("data")) {
            parser.parseCommand(response);                
            // }
            if (response != null)
                System.out.println("> " + response);
        } catch (IOException e) {
            System.out.println("Error while listening for responses: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error while parsing data: " + e.getMessage());
        }
    }
}
