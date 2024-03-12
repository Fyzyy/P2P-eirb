import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SharedFile {

    // Nom du fichier
    private String filename;

    // The shared file
    private File file;

    // Clé hexadécimale du fichier
    private String key;

    // Taille en octets du fichier
    private long size;

    // Taille en octets d'une pièce du fichier
    private int pieceSize;
    
    /**
     * Constructor of the SharedFile class
     * 
     * @param path The path to the file
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public SharedFile(String path) throws IOException, NoSuchAlgorithmException {
        this.file = new File(path);
        this.filename = this.file.getName();
        this.key = computeKey();
        this.size = Files.size(this.file.toPath());
        pieceSize = 1024;
    }

    /**
     * Takes the path of the file and computes the hexadecimal key of the file.
     * 
     * @return The hexadecimal key of the file
     * @throws IOException
     * @throws NoSuchAlgorithmException 
     */
    private String computeKey() throws IOException, NoSuchAlgorithmException {

        byte[] data = Files.readAllBytes(Paths.get(this.file.toPath().toString()));
        byte[] hash = MessageDigest.getInstance("MD5").digest(data);
        String checksum = new BigInteger(1, hash).toString(16);

        return checksum;
    }
}
