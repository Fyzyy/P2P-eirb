package src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;



public class Communication {

    private DataOutputStream sender;
    private BufferedReader reader;
    private Socket socket;

    public Communication(Socket socket) throws IOException {
        this.socket = socket;
        this.sender = new DataOutputStream(this.socket.getOutputStream());
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    public void sendMessage(String message) throws IOException{
        byte[] bytes = message.getBytes("UTF-8");
        try {
            sender.write(bytes);
            sender.flush();
        } catch (IOException e) {
            System.out.println("Cannot send message\n");
            throw e;
        }
    }

    public String receiveMessage() throws IOException {
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("Cannot read message\n");
            throw e;
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void close() throws IOException {
        this.sender.close();
        this.socket.close();
        this.reader.close();
    }
}