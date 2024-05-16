package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LogManager {

    protected String logName;

    public LogManager(int port){
        this.logName = "logs/log" + Integer.toString(port) + ".txt";
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