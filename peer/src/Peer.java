package src;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Peer {

    private List<Communication> communications;
    private Listener listener;

    public Peer(int portNumber) throws IOException {
        communications = new ArrayList<>();

        listener = new Listener(portNumber);
        listener.start();
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
                System.out.println("Disconnected from peer\n");
                return;
            }
        }
        System.out.println("Peer not found\n");
    }

    public void sendMessage(String message, InetAddress peerAddress, int peerPortNumber) {
        for (Communication communication : communications) {
            if (haveCommunication(peerAddress, peerPortNumber)) {
                try {
                    communication.sendMessage(message);
                } catch (IOException e) {
                    System.out.println("I/O error: " + e.getMessage());
                }
                return;
            }
        }
        System.out.println("Peer not found\n");
    }
}

class Communication {
    private DataOutputStream sender;
    private BufferedReader reader;
    private Socket socket;

    public Communication(Socket socket) throws IOException {
        this.socket = socket;
        this.sender = new DataOutputStream(this.socket.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    public void sendMessage(String message) throws IOException{
        byte[] bytes = message.getBytes("UTF-8");
        try {
            sender.write(bytes);
            sender.flush();
            try {
                System.out.println(reader.readLine());
            } catch (IOException e) {
                System.out.println("Cannot read message");
                throw e;
            }
        } catch (IOException e) {
            System.out.println("Cannot send message");
            throw e;
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void close() throws IOException {
        this.sender.close();
        this.socket.close();
        this.reader.close();
    }
}