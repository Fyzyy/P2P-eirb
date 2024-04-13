package tst;

import src.*;

import org.junit.Test;

import src.FileManager;

public class TestParser {
        // Exemple d'utilisation
        @Test
        public void testParsing() {

            FileManager fileManager = new FileManager();

            fileManager.addFile("fichier_test.txt");

            Parser parser = new Parser(fileManager);

            String trackerCommand = "ok";
            String trackerCommand2 = "list [$Filename1 $Length1 $PieceSize1 $Key1 $Filename2 $Length2 $PieceSize2 $Key2]";
            String trackerCommand3 = "peers $Key [$IP1:$Port1 $IP2:$Port2]";
    
            Response response1 = parser.parseCommand(trackerCommand);
            Response response2 = parser.parseCommand(trackerCommand2);
            Response response3 = parser.parseCommand(trackerCommand3);
    
            response1.print();
            response2.print();
            response3.print();
    
            
            String peerCommand = "interested 8905e92afeb80fc7722ec89eb0bf0966";
            parser.parseCommand(peerCommand);
        }
    
}
