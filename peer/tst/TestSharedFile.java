package tst;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

import src.SharedFile;

public class TestSharedFile {

    String path = "peer/data/fichier_test.txt";

    @Test
    void testSharedFileConstructor() throws IOException, NoSuchAlgorithmException {
        SharedFile sf = new SharedFile(path);
        assertFalse(sf == null);
        assertEquals(sf.getKey(), "1feb0d2009baa94dfa3b13b759c7db4f");
        assertEquals(573, sf.getSize());
    }

    @Test
    void testGetKey() throws IOException, NoSuchAlgorithmException {
        SharedFile sf = new SharedFile(path);
        String s = sf.getKey();
        s += "fzeoigr";
        assertNotEquals(sf.getKey(), s);
    }

    @Test
    void testBase64() throws IOException, NoSuchAlgorithmException {
        SharedFile sf = new SharedFile(path);
        String bm64 = sf.getBitMapBase64();
        System.out.println(bm64);
        System.out.println(sf.getBitMapString());
        System.out.println(SharedFile.Base64ToBitMapString(bm64));

        assert sf.getBitMapString().equals(SharedFile.Base64ToBitMapString(bm64));
    }
    
}
