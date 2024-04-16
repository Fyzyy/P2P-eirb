package src;

import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;


public class Response {

    private ResponseType type;
    private String message;

    
    public Response() {
    }
    
    public ResponseType getType() {
        return this.type;
    }
    
    public void setType(ResponseType type) {
        this.type = type;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void print() {
        System.out.println(this.message);
    }
    
    public void send(SocketChannel sender) {
        try {
            sender.write(ByteBuffer.wrap(this.message.getBytes()));
        } catch (Exception e) {
            System.out.println("Error while sending response: " + e.getMessage());
        }
    }
}

