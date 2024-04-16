package tst;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.Communication;

public class TestCommunication {

    private Communication communication;
    private ByteArrayOutputStream outputStream;
    private ByteArrayInputStream inputStream;

    private static class FakeSocket extends Socket {
        private ByteArrayInputStream input;
        private ByteArrayOutputStream output;

        public FakeSocket(ByteArrayInputStream input, ByteArrayOutputStream output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return input;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return output;
        }
    }

    @BeforeEach
    public void setUp() throws IOException {
        outputStream = new ByteArrayOutputStream();
        inputStream = new ByteArrayInputStream("Test Message\r\n".getBytes());
        Socket fakeSocket = new FakeSocket(inputStream, outputStream);
        communication = new Communication(fakeSocket);
    }

    @AfterEach
    public void tearDown() throws IOException {
        outputStream.close();
        inputStream.close();
    }

    @Test
    public void testSendMessage() throws IOException {
        communication.sendMessage("Test Message\r\n");
        assertEquals("Test Message\r\n", outputStream.toString("UTF-8"));
    }

    @Test
    public void testReceiveMessage() throws IOException {
        String message = communication.receiveMessage();
        assertEquals("Test Message\r\n", message);
    }
}
