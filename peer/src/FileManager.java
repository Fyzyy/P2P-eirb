package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class FileManager {
    private Map<String, SharedFile> files;
    public Map<String, SharedFile> availableFiles;

    public FileManager() {
        files = new HashMap<>();
        availableFiles = new HashMap<>();
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
            String tmp = "";
            for (int i = 0; i<file.getBitMap().length; i++){
                tmp += file.getBitMap()[i] ? "1" : "0";
            }
            System.out.println(tmp);
            System.out.println("\n");
        }
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

}
