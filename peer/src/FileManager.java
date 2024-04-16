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

    public FileManager() {
        files = new HashMap<>();
    }

    public void addFile(String path) {
        try {
            SharedFile file = new SharedFile(path);
            files.put(file.getKey(), file);
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
      

    public void writeToFile (String filename, String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            writer.write(message);
            writer.newLine();
            writer.close();
            System.out.println("Successfully wrote to log.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public boolean checkFilePresence(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

}
