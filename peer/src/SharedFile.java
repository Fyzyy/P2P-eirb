package src;
import java.io.File;
import java.io.IOException;
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

    public String getKey() {
        String copy = new String(this.key);
        return copy;
    }

    public long getSize() {
        return this.size;
    }

    public int getPieceSize() {
        return this.pieceSize;
    }

    public String getFilename() {
        String copy = new String(this.filename);
        return copy;
    }

    public File getFile() {
        File copy = new File(this.file.getAbsolutePath());
        if (copy.exists()) {
            return copy;
        }
        return null;
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
