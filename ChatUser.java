package com.netconnect.ChatRoom;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

public class ChatUser {
    private final Socket controlSocket;
    private final Socket dataSocket;
    private final PrintStream controlOutStream;
    private final InputStream controlInStream;
    private final PrintStream dataOutStream;
    private final InputStream dataInStream;
    private final String userName;
    private final String chattingWithUsername;

    public ChatUser(Socket controlSocket, Socket dataSocket, String userName, String chattingWithUsername) throws IOException {
        this.controlSocket = controlSocket;
        this.dataSocket = dataSocket;

        this.controlOutStream = new PrintStream(this.controlSocket.getOutputStream());
        this.controlInStream = this.controlSocket.getInputStream();

        this.dataOutStream = new PrintStream(this.dataSocket.getOutputStream());
        this.dataInStream = this.dataSocket.getInputStream();

        this.userName = userName;
        this.chattingWithUsername = chattingWithUsername;
    }

    public PrintStream getControlOutStream() {
        return this.controlOutStream;
    }

    public InputStream getControlInStream() {
        return this.controlInStream;
    }

    public PrintStream getDataOutStream() {
        return this.dataOutStream;
    }

    public InputStream getDataInStream() {
        return this.dataInStream;
    }

    public boolean isControlSocketClosed() {
        return this.controlSocket.isClosed();
    }

    public boolean isDataSocketClosed() {
        return this.dataSocket.isClosed();
    }

    public String getUsername() {
        return this.userName;
    }

    public String getChattingWithUsername() {
        return this.chattingWithUsername;
    }


    public void closeConnections() {
        try {
            if (!controlSocket.isClosed()) {
                controlSocket.close();
            }
            if (!dataSocket.isClosed()) {
                dataSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing sockets for user " + this.userName + ": " + e.getMessage());
        }
    }
}
