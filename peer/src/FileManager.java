package src;

import java.util.HashMap;
import java.util.Map;

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

    public void listFiles() {
        for (SharedFile file : files.values()) {
            System.out.println(file.getFilename() + " (" + file.getSize() + " bytes)" + " (" + file.getPieceSize() + " piece size)" + "(" + file.getKey()+")\n");
        }
    }

}
