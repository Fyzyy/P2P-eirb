package src;

import java.util.*;

public class Parser {

    public static void parseCommand(String command) {
        String[] parts = command.split("\\s+");
        String messageType = parts[0];
        
        switch (messageType) {

            //tracker commands
            case "ok":
                System.out.println("Confirmation du tracker : " + command);
                break;
            case "list":
                parseListCommand(parts);
                break;
            case "peers":
                parsePeersCommand(parts);
                break;

            //peer commands
            case "interested":
                parseInterestedCommand(parts);
                break;
            case "have":
                parseHaveCommand(parts);
                break;
            case "getpieces":
                parseGetPiecesCommand(parts);
                break;
            case "data":
                parseDataCommand(parts);
                break;
            default:
                System.out.println("Commande non reconnue : " + command);
        }
    }

    // Méthodes de parsing pour les commandes du Tracker
    private static void parseListCommand(String[] parts) {
        List<String> files = Arrays.asList(parts).subList(1, parts.length);
        for (int i = 0; i < files.size(); i++) {
            if (i % 4 == 0) {
                String fileName = files.get(i).replace("[", " ");
                String fileLength = files.get(i + 1);
                String pieceSize = files.get(i + 2);
                String key = files.get(i + 3).replace("]", " ");
                System.out.println("Fichier : " + fileName + ", taille : " + fileLength + ", taille des pièces : " + pieceSize + ", clé : " + key);
            }
        }
    }
    
    private static void parsePeersCommand(String[] parts) {
        String key = parts[1];
        List<String> peerList = Arrays.asList(parts).subList(2, parts.length);

        System.out.println("Clé du fichier : " + key + "\n Liste des pairs : ");
        for (int i = 0; i < peerList.size(); i++) {
            String[] peer = peerList.get(i).split(":");
            String ip = peer[0].replace("[", "");
            String port = peer[1].replace("]", "");
            System.out.println("Pair : " + ip + ", port : " + port);
        }
    }
    
    // Méthodes de parsing pour les commandes des Pairs
    private static void parseInterestedCommand(String[] parts) {
        String key = parts[1];
        System.out.println("Intérêt du pair pour le fichier avec la clé " + key);
    }
    
    private static void parseHaveCommand(String[] parts) {
        String key = parts[1];
        String bufferMap = parts[2];
        System.out.println("Disponibilité du pair pour le fichier avec la clé " + key + ", bufferMap : " + bufferMap);
    }
    
    private static void parseGetPiecesCommand(String[] parts) {
        String key = parts[1];
        List<String> pieceIndexes = Arrays.asList(parts).subList(2, parts.length);
        System.out.println("Demande de pièces du pair pour le fichier avec la clé " + key + ", indexes : " + pieceIndexes);
    }
    
    private static void parseDataCommand(String[] parts) {
        String key = parts[1];
        List<String> data = Arrays.asList(parts).subList(2, parts.length);
        System.out.println("Données reçues du pair pour le fichier avec la clé " + key + " : " + data);
    }
    
    // Exemple d'utilisation
    public static void main(String[] args) {
        String trackerCommand = "ok";
        String trackerCommand2 = "list [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2]";
        String trackerCommand3 = "peers $Key [$IP1:$Port1 $IP2:$Port2]";

        parseCommand(trackerCommand);
        parseCommand(trackerCommand2);
        parseCommand(trackerCommand3);
        
        String peerCommand = "interested 8905e92afeb80fc7722ec89eb0bf0966";
        parseCommand(peerCommand);
    }
}
