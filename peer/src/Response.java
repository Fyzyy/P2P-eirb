package src;

import java.io.DataOutputStream;

enum ResponseType {
    SUCCESS,
    ERROR
}

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

    public void send(DataOutputStream sender) {
        try {
            sender.writeBytes(this.message);
            sender.flush();
        } catch (Exception e) {
            System.out.println("Cannot send message\n");
        }
    }
}
