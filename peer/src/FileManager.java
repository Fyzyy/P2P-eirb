package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileManager {
    
    private Map<String, SharedFile> files;
    public Map<String, SharedFile> availableFiles;
    private LogManager logManager;
    private String manifestName;

    public FileManager(int logName, String manifestName) {
        files = new HashMap<>();
        availableFiles = new HashMap<>();
        logManager = new LogManager(logName);
        this.manifestName = "manifests/"+manifestName;
        createManifest("manifests/"+manifestName);
    }


    /****************** Methods to manipulate files attribute ******************/


    public void loadFile(String path) {
        try {
            SharedFile file = new SharedFile(path);
            files.put(file.getKey(), file);
        } catch (Exception e) {
            System.out.println("Cannot add file: " + e.getMessage());
        }
    }

    public void loadFile(String path, int pieceSize) {
        try {
            SharedFile file = new SharedFile(path, pieceSize);
            files.put(file.getKey(), file);
        } catch (Exception e) {
            System.out.println("Cannot add file: " + e.getMessage());
        }
    }

    public void loadFile(String path, String key) {
        try {
            SharedFile file = new SharedFile(path, key);
            files.put(key, file);
        } catch (Exception e) {
            System.out.println("Cannot add file: " + e.getMessage());
        }
    }
    
    public void loadFile(String path, int pieceSize, int size) {
        try {
            SharedFile file = new SharedFile(path, pieceSize, size);
            files.put(file.getKey(), file);
        } catch (Exception e) {
            System.out.println("Cannot add file: " + e.getMessage());
        }
    }

    public void loadFile(String path, int pieceSize, int size, String key) {
        try {
            SharedFile file = new SharedFile(path, pieceSize, size, key);
            files.put(key, file);
        } catch (Exception e) {
            System.out.println("Cannot add file: " + e.getMessage());
        }
    }

    public void removeFile(String path) {
        try {
            SharedFile file = new SharedFile(path);
            files.remove(file.getKey());
        } catch (Exception e) {
            System.out.println("Cannot remove file: " + e.getMessage());
        }
    }

    public boolean containsFile(String path) {
        try {
            SharedFile file = new SharedFile(path);
            return files.containsKey(file.getKey());
        } catch (Exception e) {
            System.out.println("Cannot check file: " + e.getMessage());
            return false;
        }
    }
    
    public boolean containsKey(String key) {
        return files.containsKey(key);
    }

    public SharedFile getFile(String path) {
        try {
            SharedFile file = new SharedFile(path);
            return files.get(file.getKey());
        } catch (Exception e) {
            System.out.println("Cannot get file: " + e.getMessage());
            return null;
        }
    }

    public String convertListToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            sb.append(str);
            sb.append(" ");
        }
        return sb.toString(); 
    }

    public String getFiles(){
        List<String> res = new ArrayList<String>();
        
        for (SharedFile file : files.values()) {
            res.add(file.getFilename());
            res.add(Long.toString(file.getSize()));
            res.add(Integer.toString(file.getPieceSize()));
            res.add(file.getKey());
        }
        return convertListToString(res);
    }

    public SharedFile getFileByKey(String key) {
        return files.get(key);
    }

    public void listFiles() {
        for (SharedFile file : files.values()) {
            System.out.println(file.getFilename() + " (" + file.getSize() + " bytes)" + " (" + file.getPieceSize() + " piece size)" + "(" + file.getKey()+")\n");
        }
    }

    public void getBitMap() {
        for (SharedFile file : files.values()) {
            System.out.println(file.getFilename() + ":" );
            String tmp = file.getBitMapString() + " ";
            System.out.println(tmp);
            System.out.println("\n");
        }
    }

    public List<String> getStatusInfo() {
        List<String> result = new ArrayList<String>();
        String tmp;
        for (SharedFile file : files.values()) {
            tmp = "";
            tmp  += "have " + file.getKey() + " " ;
            tmp += file.getBitMapString();
            // tmp += "\n";
            result.add(tmp);
        }
        return result;
    }

    public List<String> getStatusInfoTracker() {
        List<String> result = new ArrayList<String>();
        String tmp;
        for (SharedFile file : files.values()) {
            tmp  = file.getKey();
            result.add(tmp);
        }
        return result;
    }

    /****************** Methods to manipulate availableFiles ******************/


    public void addAvailableFile(String key, String filename) {
        try {
            SharedFile file = new SharedFile(filename, key);
            availableFiles.put(key, file);
        } catch (Exception e) {
            System.out.println("Cannot add file in addAvailableFile: " + e.getMessage());
        }
        System.out.println("Add tmp file");
    }

    public void removeAvailableFile(String key) {
        availableFiles.remove(key);
        System.out.println("Tmp file removed");
    }

    public SharedFile getAvailableFilenameByKey(String key){
        return availableFiles.get(key);
    }

    public void createTmpFile(String filename, String key){
        try {
            File myObj = new File(filename);
            if (myObj.createNewFile()) {
                loadFile(filename, key);
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            addAvailableFile(key, filename);
            System.out.println("Tmp file created");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public boolean containsAvailableKey(String key){
        return availableFiles.containsKey(key);
    }

    public void printAvailableFile(){
        System.out.println(availableFiles);
    }


    /****************** Methods to manipulate real files ******************/


    public void createFile(String filename) {
        try {
            File myObj = new File(filename);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
        
    public void createFile(String filename, int pieceSize, int size) {
        try {
            File myObj = new File(filename);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
                loadFile(filename, pieceSize, size);
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void createFile(String filename, String key) {
        try {
            File myObj = new File(filename);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
                loadFile(filename, key);
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void createFile(String filename, int pieceSize, int size, String key) {
        try {
            File myObj = new File(filename);
            if (myObj.createNewFile()) {
                loadFile(filename, pieceSize, size, key);
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
      
    public void writeToFile(String filename, String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            writer.write(message);
            writer.newLine();
            writer.close();
            // System.out.println("Successfully wrote to log.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeToFileNoLine(String filename, String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            writer.write(message);
            // writer.newLine();
            writer.close();
            // System.out.println("Successfully wrote to log.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void moveFileToData(String key){
        File file = getAvailableFilenameByKey(key).getFile();
        file.renameTo(new File("data/"+file.getName()));
        System.out.println("File moved from tmp to data");
    }

    public boolean checkFilePresence(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public String[] readLinesFromFile(String fileName) {
        String filePath = fileName;
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
        return lines.toArray(new String[0]);
    }

    public boolean checkWordPresenceInFile(String fileName, String word) {
        String[] array = readLinesFromFile(fileName);
        for (String str : array) {
            if (str.equals(word)) {
                return true;
            }
        }
        return false;
    }

    public void removeFromFile(String fileName, String message) throws IOException{
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line;

            if (checkWordPresenceInFile(fileName, message)){

                while ((line = reader.readLine()) != null) {
                    line = line.replaceAll(message, "");
                    sb.append(line).append("\n");
                }
                reader.close();
                
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                writer.write(sb.toString());
                writer.close();
                
                // System.out.println("Le mot \"" + message + "\" a été effacé du fichier.");
            }
            
            else{
                // System.out.println("File not present in log");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la manipulation du fichier : " + e.getMessage());
        }
    }


    /* ************* Methods to manipulate logs ************* */
    

    public void loadLog(){
        String[] files = logManager.readLinesFromLog();
        for (int i = 0; i<files.length; i++){
            loadFile(files[i]);
        }
        System.out.println("Log file loaded\n");
    }

    public void createLog(){
        if (checkFilePresence(logManager.logName) == false){
            createFile(logManager.logName);
            System.out.println("Log file created\n");
        }
        else{
            System.out.println("Log file already exists");
            loadLog();
        }
    }

    public void writeLog(String message){
        writeToFile(logManager.logName, message);
    }

    public void removeFromLog(String message) throws IOException{
        removeFromFile(logManager.logName, message);
    }

    public boolean checkWordPresenceInLog(String word) {
        return logManager.checkWordPresenceInLog(word);
    }


    /* ************* Methods to manipulate manifests ************* */

    static void createManifest(String manifestName){
        try {
            File myObj = new File(manifestName);
            if (myObj.createNewFile()) {
                // loadFile(fileName, pieceSize, fileSize, key);
                System.out.println("Manifest created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeToManifest(String fileName, int pieceSize, long fileSize, String key){
        String message = "Filename: " + fileName + " Piece Size: " + Integer.toString(pieceSize) + " Size: " + Long.toString(fileSize) + " Key: " + key;
        writeToFile(manifestName, message);
    }

    public void removeToManifest(String fileName, int pieceSize, long fileSize, String key) throws IOException{
        String message = "Filename: " + fileName + " Piece Size: " + Integer.toString(pieceSize) + " Size: " + Long.toString(fileSize) + " Key: " + key;
        removeFromFile(manifestName, message);
    }

}
