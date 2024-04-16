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
        //System.out.println("Connected to " + socket.getInetAddress().toString() + ":" + socket.getPort() + "\n");
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
        StringBuilder messageBuilder = new StringBuilder();
        int prevChar = -1;
        int character;
        try {
            while ((character = reader.read()) != -1) {
                if (prevChar == '\r' && character == '\n') {
                    messageBuilder.append((char) character);
                    break; // Message complet reçu
                }
                messageBuilder.append((char) character);
                prevChar = character;
            }
            return messageBuilder.toString();
        } catch (IOException e) {
            System.out.println("Cannot read message\n");
            throw e;
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    public boolean equals(Communication communication) {
        return this.socket.getInetAddress().equals(communication.getSocket().getInetAddress()) && this.socket.getPort() == communication.getSocket().getPort();
    }

    public void close() throws IOException {
        this.sender.close();
        this.socket.close();
        this.reader.close();
    }
}