package src;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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
                    response.setType(ResponseType.NO_RESPONSE);
                    break;
                case "list":
                    parseListCommand(parts);
                    response.setType(ResponseType.NO_RESPONSE);
                    break;
                case "peers":
                    parsePeersCommand(parts);
                    response.setType(ResponseType.NO_RESPONSE);
                    break;

                //peer commands
                case "interested":
                    parseInterestedCommand(parts, response);
                    break;
                case "have":
                    parseHaveCommand(parts, response);
                    break;
                case "getpieces":
                    parseGetPiecesCommand(parts, response);
                    break;
                case "data":
                    parseDataCommand(parts, response);
                    break;
                default:
                    System.out.println("Commande non reconnue : " + command);
            }
            return response;

        } catch (Exception e) {
            throw new Exception("Erreur lors du parsing de la commande : " + e.getMessage());
        }
    }
    
    // Méthodes de parsing pour les commandes du Tracker
    
    // > list [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2]
    
    private  void parseListCommand(String[] parts) throws IOException, NoSuchAlgorithmException {
        List<String> files = Arrays.asList(parts).subList(1, parts.length);
        for (int i = 0; i < files.size(); i++) {
            if (i % 4 == 0) {
                String fileName = files.get(i).replace("[", "");
                String size = files.get(i + 1);
                String pieceSize = files.get(i + 2);
                String key = files.get(i + 3).replace("]", "");
                fileManager.createTmpFile("tmp/"+fileName, key);
                // fileManager.addAvailableFile("tmp/"+fileName, key);
                System.out.println("Fichier : " + fileName + ", taille : " + size + ", taille des pièces : " + pieceSize + ", clé : " + key);
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
    
    // Méthodes de parsing pour les commandes des Pairs
    
    // < interested $Key
    // > have $Key $BufferMap
    private void parseInterestedCommand(String[] parts, Response response) {
        String key = parts[1];
    
        if (fileManager.containsKey(key)) {
            response.setType(ResponseType.HAVE);
            response.setMessage("have " + key + " [" + fileManager.getFileByKey(key).getBitMapString() + "]\r\n" );
        }
        else {
            response.setType(ResponseType.UNKNOW);
            response.setMessage("Unknow key\r\n");
        }        
        return;
    }

    // < have $Key $BufferMap
    // > have $Key $BufferMap
    private  void parseHaveCommand(String[] parts, Response response) {
        String key = parts[1];
        String bufferMap = parts[2];
        System.out.println("Disponibilité du pair pour le fichier avec la clé " + key + ", bufferMap : " + bufferMap);

        if (fileManager.containsKey(key)) {
            response.setType(ResponseType.HAVE);
            response.setMessage("have " + key + " [" + fileManager.getFileByKey(key).getBitMapString() + "]\r\n");            
        }
        else {
            response.setType(ResponseType.UNKNOW);
            response.setMessage("Unknow key\r\n");
        }

    }
    
    // < getpieces $Key [$Index1 $Index2 $Index3 …]
    // > data $Key [$Index1:$Piece1 $Index2:$Piece2 $Index3:$Piece3 …] //build the response message
    private void parseGetPiecesCommand(String[] parts, Response response) {
        String key = parts[1];
        List<String> pieceIndexes = new ArrayList<>();

        // Extracting piece indexes from the nested list
        for (int i = 2; i < parts.length; i++) {
            String index = parts[i].replaceAll("\\[|\\]", ""); // Removing '[' and ']' characters
            pieceIndexes.add(index);
        }
        
        if (fileManager.containsKey(key)) {
            List<String> pieces = new ArrayList<>();
            for (String index : pieceIndexes) {
                int pieceIndex = Integer.parseInt(index);
                byte[] pieceData = fileManager.getFileByKey(key).getPiece(pieceIndex);
                if (pieceData != null) {
                    String piece = pieceIndex + ":" + "%" + new String(pieceData) + "%";
                    pieces.add(piece);
                } else {
                    System.out.println("Piece " + pieceIndex + " is missing for key " + key);
                }
            }
            
            String responseData = String.join(" ", pieces);
            response.setType(ResponseType.DATA);
            response.setMessage("data " + key + " [" + responseData + "]\r\n");
        } else {
            response.setType(ResponseType.UNKNOW);
            response.setMessage("Unknow key\r\n");
            System.out.println("Unknow key");
        }
    }
    
    // data $Key [$Index1:$Piece1 $Index2:$Piece2 $Index3:$Piece3 …]
    private void parseDataCommand(String[] parts, Response response) {

        String key = parts[1];
        SharedFile file = fileManager.getAvailableFilenameByKey(key);
        String fileName = "tmp/" + file.getFilename();
        System.out.println("file : " + file);

        if (fileManager.containsAvailableKey(key)) {
            for (int i = 2; i < parts.length; i++) {
                // Remove brackets from the piece
                String pieceStr = parts[i].replaceAll("[\\[\\]]", "");
                String[] pieceSplit = pieceStr.split(":");
                int pieceIndex = Integer.parseInt(pieceSplit[0]);

                // Concatenate parts split by space until the piece is complete
                StringBuilder fullPieceBuilder = new StringBuilder(pieceSplit[1]);
                while (!fullPieceBuilder.toString().endsWith("%")) {
                    i++;
                    if (i >= parts.length) {
                        // If we reach the end without finding the end marker, break the loop
                        break;
                    }
                    fullPieceBuilder.append(" ").append(parts[i]);
                    fileManager.writeToFileNoLine(fileName, "\n");
                    fileManager.writeToFileNoLine(fileName, parts[i].replaceAll("%", ""));
                    // System.out.println(parts[i]);
                }

                // Remove '%' from the end of the full piece
                String fullPiece = fullPieceBuilder.toString().replaceAll("%", "").replaceAll("]", "");
                
                byte[] pieceData = fullPiece.getBytes();
                System.out.println(fileManager.getAvailableFilenameByKey(key));
                fileManager.loadFile(fileName, key);
                fileManager.getFileByKey(key).setPiece(pieceIndex, pieceData);
                System.out.println("Piece " + pieceIndex + " received for key " + key);
                System.out.println("Piece data : " + new String(pieceData));
            }

            fileManager.moveFileToData(key);
            fileManager.removeAvailableFile(key);
            fileManager.writeLog("data/"+file.getFilename());

            response.setType(ResponseType.NO_RESPONSE);
        } else {
            response.setType(ResponseType.UNKNOW);
            response.setMessage("Unknown key\r\n");
        }
    }


}  