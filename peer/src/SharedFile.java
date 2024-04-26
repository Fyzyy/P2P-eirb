package src;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.FileNotFoundException;

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

    // Tableau de bits représentant les pièces du fichier
    private boolean[] bitMap;

    // data in pieces
    private byte[][] data;
    
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
        splitFile();
    }

    public SharedFile(String path, int pieceSize) throws IOException, NoSuchAlgorithmException {
        this.file = new File(path);
        this.filename = this.file.getName();
        this.key = computeKey();
        this.size = Files.size(this.file.toPath());
        this.pieceSize = pieceSize;
        splitFile();
    }

    public SharedFile(String path, int pieceSize, int size) throws IOException, NoSuchAlgorithmException {
        this.file = new File(path);
        this.filename = this.file.getName();
        this.key = computeKey();
        this.size = size;
        this.pieceSize = pieceSize;
        splitFile();
    }

    public SharedFile(String path, int pieceSize, int size, String key) throws IOException, NoSuchAlgorithmException {
        this.file = new File(path);
        this.filename = this.file.getName();
        this.key = key;
        this.size = size;
        this.pieceSize = pieceSize;
        splitFile();
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

    public boolean[] getBitMap() {
        return this.bitMap;
    }

    public String getBitMapString() {
        String bitMapString = "";
        for (int i = 0; i < this.bitMap.length; i++) {
            bitMapString += this.bitMap[i] ? "1" : "0";
        }
        return bitMapString;
    }

    public byte[][] getData() {
        return this.data;
    }

    public byte[] getPiece(int index) {
        if (index < 0 || index >= this.data.length) {
            return null;
        }
        if (!this.bitMap[index]) {
            return null;
        }
        
        return this.data[index];
    }

    public void setPiece(int index, byte[] piece) {
        this.data[index] = piece;
        this.bitMap[index] = true;
    }



    private void splitFile() throws IOException {
        try {
            int pieces = (int) Math.ceil((double) this.size / this.pieceSize);
            this.bitMap = new boolean[pieces];
            this.data = new byte[pieces][this.pieceSize];
    
            byte[] fileData = Files.readAllBytes(Paths.get(this.file.toPath().toString()));
    
            for (int i = 0; i < pieces; i++) {
                int length = this.pieceSize;
                if (i == pieces - 1) {
                    length = (int) (this.size % this.pieceSize);
                }
                byte[] piece = new byte[length];
                System.arraycopy(fileData, i * this.pieceSize, piece, 0, length);
                this.data[i] = piece;
                bitMap[i] = true;
            }
        } catch (FileNotFoundException e) {
            // Handle file not found exception
            System.err.println("File not found: " + e.getMessage());
        }
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

