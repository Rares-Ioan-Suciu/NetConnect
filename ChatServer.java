package com.netconnect.ChatRoom;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatServer {

    private final int controlPort;
    private final int dataPort;
    private final List<ChatUser> allUsers = new ArrayList<>();
    private ServerSocket controlServerSocket;
    private ServerSocket dataServerSocket;

    public ChatServer(int controlPort, int dataPort) {
        this.controlPort = (controlPort == 0) ? findAvailablePort() : controlPort;
        this.dataPort = (dataPort == 0) ? findAvailablePort() : dataPort;
    }

    private int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("No available ports", e);
        }
    }

    public void startServer() {
        try {
            controlServerSocket = new ServerSocket(this.controlPort, 50, InetAddress.getByName("0.0.0.0"));
            dataServerSocket = new ServerSocket(this.dataPort, 50, InetAddress.getByName("0.0.0.0"));

            System.out.println("Chat server started:");
            System.out.println("\tControl port: " + this.controlPort);
            System.out.println("\tData port: " + this.dataPort);

            acceptUsers();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(1);
        }
    }

    private void acceptUsers() {
        while (!controlServerSocket.isClosed() && !dataServerSocket.isClosed()) {
            try {

                Socket controlSocket = controlServerSocket.accept();
                Socket dataSocket = dataServerSocket.accept();

                System.out.println("New control connection: " + controlSocket.getRemoteSocketAddress());
                System.out.println("New data connection: " + dataSocket.getRemoteSocketAddress());


                JSONObject userDetails = parseUserDetails(controlSocket);

                if (userDetails == null) {
                    System.out.println("Failed to retrieve user details. Closing connection.");
                    controlSocket.close();
                    dataSocket.close();
                    continue;
                }

                String username = (String) userDetails.get("username");
                String chattingWithUsername = (String) userDetails.get("chattingWithUsername");

                System.out.println("New Client: \"" + username + "\" chatting with \"" + chattingWithUsername + "\"");

                ChatUser newUser = new ChatUser(controlSocket, dataSocket, username, chattingWithUsername);
                this.allUsers.add(newUser);


                new Thread(new Receiver(this, newUser)).start();
                new Thread(new DataHandler(newUser)).start();

            } catch (IOException e) {
                if (!controlServerSocket.isClosed() || !dataServerSocket.isClosed()) {
                    System.err.println("Error accepting user connections: " + e.getMessage());
                }
            }
        }
    }

    private JSONObject parseUserDetails(Socket controlSocket) {
        try {
            Scanner scanner = new Scanner(controlSocket.getInputStream());
            if (scanner.hasNextLine()) {
                String rawMessage = scanner.nextLine();
                return (JSONObject) new JSONParser().parse(rawMessage);
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error parsing user details: " + e.getMessage());
        }
        return null;
    }

    public List<ChatUser> getAllUsers() {
        return this.allUsers;
    }

    public int getControlPort() {
        return this.controlPort;
    }

    public int getDataPort() {
        return this.dataPort;
    }

    public void stopServer() {
        try {
            if (controlServerSocket != null && !controlServerSocket.isClosed()) {
                System.out.println("Stopping control server...");
                controlServerSocket.close();
            }
            if (dataServerSocket != null && !dataServerSocket.isClosed()) {
                System.out.println("Stopping data server...");
                dataServerSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to close server sockets: " + e.getMessage());
        }
    }
}
