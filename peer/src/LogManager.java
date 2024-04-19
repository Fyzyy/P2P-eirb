package src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LogManager {

    private String logName;

    public LogManager(int port){
        this.logName = "logs/log" + Integer.toString(port) + ".txt";
    }

    public void loadLog(FileManager fileManager){
        String[] files = readLinesFromLog();
        for (int i = 0; i<files.length; i++){
            fileManager.addFile(files[i]);
        }
        System.out.println("Log file loaded\n");
    }

    public void createLog(FileManager fileManager){
        if (fileManager.checkFilePresence(logName) == false){
            fileManager.createFile(logName);
            System.out.println("Log file created\n");
        }
        else{
            System.out.println("Log file already exists");
            loadLog(fileManager);
        }
    }

    public void writeLog(String message, FileManager fileManager){
        fileManager.writeToFile(logName, message);
    }

    public void removeFromLog(String message) throws IOException{
        try {
            BufferedReader reader = new BufferedReader(new FileReader(logName));
            StringBuilder sb = new StringBuilder();
            String line;

            if (checkWordPresenceInLog(message)){

                while ((line = reader.readLine()) != null) {
                    line = line.replaceAll(message, "");
                    sb.append(line).append("\n");
                }
                reader.close();
                
                BufferedWriter writer = new BufferedWriter(new FileWriter(logName));
                writer.write(sb.toString());
                writer.close();
                
                System.out.println("Le mot \"" + message + "\" a été effacé du fichier.");
            }
            
            else{
                System.out.println("File not present in log");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la manipulation du fichier : " + e.getMessage());
        }
    }

    public boolean checkWordPresenceInLog(String word) {
        String[] array = readLinesFromLog();
        for (String str : array) {
            if (str.equals(word)) {
                return true;
            }
        }
        return false;
    }

    public String[] readLinesFromLog() {
        String filePath = logName;
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
        
}