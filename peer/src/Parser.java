package src;

import java.util.*;

public class Parser {

    private FileManager fileManager;

    public Parser(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Response parseCommand(String command) throws Exception{
        try {
            String[] parts = command.split("\\s+");
            String messageType = parts[0];
            Response response = new Response();

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
                    parseInterestedCommand(parts, response);
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
            return response;

        } catch (Exception e) {
            throw new Exception("Erreur lors du parsing de la commande : " + e.getMessage());
        }
    }
    
    // Méthodes de parsing pour les commandes des Pairs
    private void parseInterestedCommand(String[] parts, Response response) {
        String key = parts[1];
        System.out.println("Intérêt du pair pour le fichier avec la clé " + key);

        if (fileManager.containsFile(key)) {
            response.setType(ResponseType.SUCCESS);
            response.setMessage("have" + key + " " + fileManager.getFile(key).getBitMap().toString());
        }
        else {
            response.setType(ResponseType.ERROR);
            response.setMessage("Key not found");
        }        
        return;
    }
    
    // Méthodes de parsing pour les commandes du Tracker
    private  void parseListCommand(String[] parts) {
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
    
    private  void parsePeersCommand(String[] parts) {
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
    
    private  void parseHaveCommand(String[] parts) {
        String key = parts[1];
        String bufferMap = parts[2];
        System.out.println("Disponibilité du pair pour le fichier avec la clé " + key + ", bufferMap : " + bufferMap);
    }
    
    private  void parseGetPiecesCommand(String[] parts) {
        String key = parts[1];
        List<String> pieceIndexes = Arrays.asList(parts).subList(2, parts.length);
        System.out.println("Demande de pièces du pair pour le fichier avec la clé " + key + ", indexes : " + pieceIndexes);
    }
    
    private  void parseDataCommand(String[] parts) {
        String key = parts[1];
        List<String> data = Arrays.asList(parts).subList(2, parts.length);
        System.out.println("Données reçues du pair pour le fichier avec la clé " + key + " : " + data);
    }
}

