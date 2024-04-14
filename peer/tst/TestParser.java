package tst;

import src.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestParser {

    private FileManager fileManager;
    private Parser parser;

    @Before
    public void setUp() {
        fileManager = new FileManager();
        parser = new Parser(fileManager);
        fileManager.addFile("fichier_test.txt");
    }

    @Test
    public void testTrackerParsingOkCommand() {
        String trackerCommand = "ok";
        try {
            Response response = parser.parseCommand(trackerCommand);
            assertNotNull(response);
            assert response.getType() == ResponseType.NO_RESPONSE;
            response.print();
            // Ajoutez des assertions supplémentaires si nécessaire
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
            // Ajoutez des assertions supplémentaires si nécessaire
        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }

    @Test
    public void testTrackerParsingPeersCommand() {
        String trackerCommand = "peers $Key [$IP1:$Port1 $IP2:$Port2]";
        try {
            Response response = parser.parseCommand(trackerCommand);
            assertNotNull(response);
            // Ajoutez des assertions supplémentaires si nécessaire
        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }

    @Test
    public void testPeerParsingInterestedCommand() {
        String peerCommand = "interested $key";
        try {
            Response response = parser.parseCommand(peerCommand);
            assertNotNull(response);
            // Ajoutez des assertions supplémentaires si nécessaire
        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }

    @Test
    public void testPeerParsingHaveCommand() {
        String peerCommand = "have $Key $BufferMap";
        try {
            Response response = parser.parseCommand(peerCommand);
            assertNotNull(response);
            // Ajoutez des assertions supplémentaires si nécessaire
        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }

    @Test
    public void testPeerParsingGetPiecesCommand() {
        String peerCommand = "getpieces $Key [$Index1 $Index2 $Index3]";
        try {
            Response response = parser.parseCommand(peerCommand);
            assertNotNull(response);
            // Ajoutez des assertions supplémentaires si nécessaire
        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }

    @Test
    public void testPeerParsingDataCommand() {
        String peerCommand = "data $Key [$Index1:$Piece1 $Index2:$Piece2 $Index3:$Piece3]";
        try {
            Response response = parser.parseCommand(peerCommand);
            assertNotNull(response);
            // Ajoutez des assertions supplémentaires si nécessaire
        } catch (Exception e) {
            fail("Une exception a été levée : " + e.getMessage());
        }
    }
}
