package src;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer {

    private HashSet<Communication> communications;
    private FileManager fileManager;
    private Listener listener;
    private Parser parser;
    private int TrackerPort;
    private InetAddress TrackerAddress;
    private ExecutorService executor = Executors.newFixedThreadPool(5);

    public Peer(String ip, int portNumber, int TrackerPort, InetAddress TrackerAddress) throws IOException {

        communications = new HashSet<Communication>();
        fileManager = new FileManager(portNumber, "manifest" + ip.toString() + Integer.toString(portNumber));
        parser = new Parser(fileManager);

        listener = new Listener(ip, portNumber, parser);
        listener.start();
        fileManager.createLog();

        this.TrackerPort = TrackerPort;
        this.TrackerAddress = TrackerAddress;
    }

    public Boolean haveCommunication(InetAddress peerAddress, int peerPortNumber) {
        for (Communication communication : communications) {
            if (communication.getSocket().getInetAddress().equals(peerAddress)
                    && communication.getSocket().getPort() == peerPortNumber) {
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
            if (communication.getSocket().getInetAddress().equals(peerAddress)
                    && communication.getSocket().getPort() == peerPortNumber) {
                // Send
                try {
                    communication.sendMessage(message);
                    listener.setHaveSendMessage();
                    executor.submit(new ResponseListener(communication, parser));

                } catch (IOException e) {
                    System.out.println("I/O error: " + e.getMessage());
                    try {
                        System.out.println("Fermeture de la communication...");
                        this.disconnect(peerAddress, peerPortNumber);
                    } catch (IOException ex) {
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
            if (communication.getSocket().getInetAddress().equals(peerAddress)
                    && communication.getSocket().getPort() == peerPortNumber) {
                // Receive
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
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            System.out.println("File sent");
            fis.close();
        } catch (IOException e) {
            System.out.println("Error: Cannot send file");
        }
    }

    public String getFiles() {
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
            if (!fileManager.checkWordPresenceInLog(filePath)) {
                fileManager.writeLog(filePath);
            }
            fileManager.loadFile(filePath);
            System.out.println("Done");
        } catch (Exception e) {
            System.out.println("Cannot add file to peer storage");
        }
    }

    public void loadFile(String filePath, int pieceSize) {
        try {
            System.out.println("Adding file " + filePath + " to peer storage...");
            if (!fileManager.checkWordPresenceInLog(filePath)) {
                fileManager.writeLog(filePath);
            }
            fileManager.loadFile(filePath, pieceSize);
            ;
            System.out.println("Done");
        } catch (Exception e) {
            System.out.println("Cannot add file to peer storage");
        }
    }

    public void removeFile(String filePath) {
        try {
            System.out.println("Removing " + filePath + " to peer stockage...");
            fileManager.removeFile(filePath);
            fileManager.removeFromLog(filePath);
            System.out.println("Done");
        } catch (Exception e) {
            System.out.println("Cannot remove the file");
        }
    }

    public void displayPeers() {
        System.out.println("List of connected peers:");
        for (Communication communication : communications) {
            System.out.println(communication.getSocket().getInetAddress().toString().replace("/", "") + ":"
                    + communication.getSocket().getPort());
        }
    }

    public void informState() {
        if (!communications.isEmpty()) {
            for (Communication communication : communications) {
                InetAddress address = communication.getSocket().getInetAddress();
                int port = communication.getSocket().getPort();
                if (address == TrackerAddress && port == TrackerPort) {
                    sendMessage(fileManager.getUpdateInfoTracker(), address, port);
                    continue;
                }
                List<String> fileList = fileManager.getStatusInfo();
                for (int i = 0; i < fileList.size(); i++) {
                    sendMessage(fileList.get(i), address, port);
                }
            }
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
            if (response.equals("\r\n")) {
                System.out.println("> No response\n");
                return;
            }
            parser.parseCommand(response);
            if (response != null)
                System.out.println("> " + response);
        } catch (IOException e) {
            System.out.println("Error while listening for responses: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error while parsing data: " + e.getMessage());
        }
    }
}
