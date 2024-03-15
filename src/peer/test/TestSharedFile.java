package test;

import peer.SharedFile;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

public class TestSharedFile {

    @Test
    void testSharedFileConstructor() throws IOException, NoSuchAlgorithmException {
        SharedFile sf = new SharedFile("src/peer/test/fichier_test.txt");
        assertFalse(sf == null);
        assertEquals(sf.getKey(), "1feb0d2009baa94dfa3b13b759c7db4f");
        assertEquals(573, sf.getSize());
    }

    @Test
    void testGetKey() throws IOException, NoSuchAlgorithmException {
        SharedFile sf = new SharedFile("src/peer/test/fichier_test.txt");
        String s = sf.getKey();
        s += "fzeoigr";
        assertNotEquals(sf.getKey(), s);
    }
    
}