package tst;

import src.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestParser {

    private FileManager fileManager;
    private Parser parser;
    private String key;
    private String key_long;

    @Before
    public void setUp() {
        fileManager = new FileManager();
        parser = new Parser(fileManager);
        fileManager.addFile("data/fichier_test.txt");
        fileManager.addFile("data/file1.txt");
        fileManager.listFiles();
        key = fileManager.getFile("data/fichier_test.txt").getKey();
        key_long = fileManager.getFile("data/file1.txt").getKey();
    }

    @Test
    public void testTrackerParsingOkCommand() {
        String trackerCommand = "ok";
        try {
            Response response = parser.parseCommand(trackerCommand);
            assertNotNull(response);
            assert response.getType() == ResponseType.NO_RESPONSE;
            response.print();

        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }

    @Test
    public void testTrackerParsingListCommand() {
        String trackerCommand = "list [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2]";
        try {
            Response response = parser.parseCommand(trackerCommand);
            assertNotNull(response);
            response.print();
            assert response.getType() == ResponseType.NO_RESPONSE;

        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }

    @Test
    public void testTrackerParsingPeersCommand() {
        String trackerCommand = "peers " + key + " [$IP1:$Port1 $IP2:$Port2]";
        try {
            Response response = parser.parseCommand(trackerCommand);
            assertNotNull(response);
            response.print();
            assert response.getType() == ResponseType.NO_RESPONSE;

        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }

    @Test
    public void testPeerParsingInterestedCommand() {
        String peerCommand = "interested " + key;
        try {
            Response response = parser.parseCommand(peerCommand);
            assertNotNull(response);
            response.print();
            assert response.getType() == ResponseType.HAVE;
            assert response.getMessage().equals("have " + key + " [1]");
            

        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }

    @Test
    public void testPeerParsingHaveCommand() {
        String peerCommand = "have "+ key + " [" +fileManager.getFileByKey(key).getBitMapString() + "]";
        try {
            Response response = parser.parseCommand(peerCommand);
            assertNotNull(response);
            response.print();
            assert response.getType() == ResponseType.HAVE;
            assert response.getMessage().equals("have "+ key + " [" +fileManager.getFileByKey(key).getBitMapString() + "]");
            // Ajoutez des assertions supplémentaires si nécessaire
        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }

    @Test
    public void testPeerParsingGetPiecesCommand() {
        String peerCommand = "getpieces "+key_long+" [0 3 5]";
        try {
            Response response = parser.parseCommand(peerCommand);
            assertNotNull(response);
            System.out.println("\n\n RESPONSE \n\n");
            response.print();
            assert response.getType() == ResponseType.DATA;
        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }

    @Test
    public void testPeerParsingDataCommand() {

        String peerCommand = "data "+key_long+" [0:%Pi  ec  e1% 3:%P i  ece2% 4:%Pi  ece 3%]";
        try {
            Response response = parser.parseCommand(peerCommand);
            assertNotNull(response);
            assert response.getType() == ResponseType.NO_RESPONSE;
            // Ajoutez des assertions supplémentaires si nécessaire
        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }
}
