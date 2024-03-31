package src;

import java.util.*;

public class Parser {
    // Méthode pour parser les commandes provenant du tracker
    public static void parseTrackerCommand(String command) {
        String[] parts = command.split("\\s+");
        String messageType = parts[0];
        
        switch (messageType) {
            case "ok":
                System.out.println("Confirmation du tracker : " + command);
                break;
            case "list":
                parseListCommand(parts);
                break;
            case "peers":
                parsePeersCommand(parts);
                break;
            default:
                System.out.println("Commande non reconnue du tracker : " + command);
        }
    }
    
    // Méthode pour parser les commandes provenant des pairs
    public static void parsePeerCommand(String command) {
        String[] parts = command.split("\\s+");
        String messageType = parts[0];
        
        switch (messageType) {
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
                System.out.println("Commande non reconnue du pair : " + command);
        }
    }
    
    // Méthodes de parsing pour les commandes du Tracker
    private static void parseListCommand(String[] parts) {
        List<String> files = Arrays.asList(parts).subList(1, parts.length);
        System.out.println("Liste des fichiers : " + files);
    }
    
    private static void parsePeersCommand(String[] parts) {
        String key = parts[1];
        List<String> peerList = Arrays.asList(parts).subList(2, parts.length);
        System.out.println("Liste des pairs pour la clé " + key + " : " + peerList);
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
        parseTrackerCommand(trackerCommand);
        
        String peerCommand = "< interested 8905e92afeb80fc7722ec89eb0bf0966";
        parsePeerCommand(peerCommand);
    }
}
