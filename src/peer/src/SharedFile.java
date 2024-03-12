import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SharedFile {

    // Nom du fichier
    private String filename;

    // Clé hexadécimale du fichier
    private String key;

    // Taille en octets du fichier
    private int size;

    // Taille en octets d'une pièce du fichier
    private int pieceSize;
    
    public SharedFile(String filename) {
        this.filename = filename;
        this.key = computeKey();

    }

    private String computeKey() {

        String command = "md5sum " + this.filename;

        ProcessBuilder pb = new ProcessBuilder();
        pb.command("bash", "-c", command);
        Process p;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        // Lecture de la sortie de la commande
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        
        return "";
    }
}
