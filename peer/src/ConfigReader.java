package src;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;

public class ConfigReader {

    Properties properties = new Properties();
    FileInputStream inputStream = null;
    InetAddress TrackerAddress;
    int TrackerPort;
    int RefreshRate; 

    public void Parse() {
        System.out.println("Parsing config.ini...");

        try {
            inputStream = new FileInputStream("config.ini");
            properties.load(inputStream);

            // Lire les valeurs à partir du fichier de configuration
            TrackerAddress = InetAddress.getByName(properties.getProperty("tracker-address"));
            TrackerPort = Integer.parseInt(properties.getProperty("tracker-port"));
            RefreshRate = Integer.parseInt(properties.getProperty("refresh-rate"));

            // Afficher les valeurs lues
            System.out.println("Paramètre 1: " + TrackerAddress);
            System.out.println("Paramètre 2: " + TrackerPort);
            System.out.println("Paramètre 3: " + RefreshRate);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
