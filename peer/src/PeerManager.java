package src;

import java.util.ArrayList;
import java.util.List;

public class PeerManager {
    private List<PeerConnection> connectedPeers;

    public PeerManager() {
        connectedPeers = new ArrayList<>();
    }

    public void connectPeer(String ipAddress, int port) {
        connectedPeers.add(new PeerConnection(ipAddress, port));
        System.out.println("Peer connected: " + ipAddress + ":" + port);
    }

    public void disconnectPeer(String ipAddress, int port) {
        for (PeerConnection peer : connectedPeers) {
            if (peer.getIpAddress().equals(ipAddress) && peer.getPort() == port) {
                connectedPeers.remove(peer);
                System.out.println("Peer disconnected: " + ipAddress + ":" + port);
                return;
            }
        }
        System.out.println("Peer not found: " + ipAddress + ":" + port);
    }

    public void disconnectAllPeers() {
        connectedPeers.clear();
        System.out.println("All peers disconnected");
    }

    public boolean isConnected(String ipAddress, int port) {
        for (PeerConnection peer : connectedPeers) {
            if (peer.getIpAddress().equals(ipAddress) && peer.getPort() == port) {
                return true;
            }
        }
        return false;
    }

    public void printConnectedPeers() {
        if (connectedPeers.isEmpty()) {
            System.out.println("No connected peers");
            return;
        }
        System.out.println("Connected peers:");
        for (PeerConnection peer : connectedPeers) {
            System.out.println(peer.getIpAddress() + ":" + peer.getPort());
        }
    }

    public static void main(String[] args) {
        PeerManager peerManager = new PeerManager();

        // Exemple de connexion et de déconnexion de pairs
        peerManager.connectPeer("127.0.0.1", 7777);
        peerManager.connectPeer("192.168.1.1", 8888);
        peerManager.printConnectedPeers();
        peerManager.disconnectPeer("127.0.0.1", 7777);
        peerManager.printConnectedPeers();
    }
}

final class PeerConnection {
    private String ipAddress;
    private int port;

    public PeerConnection(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }
}
