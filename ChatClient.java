package com.netconnect.ChatRoom;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ChatClient {
    private Socket controlSocket;
    private Socket dataSocket;
    private Scanner controlScanner;
    private PrintStream controlOutputStream;
    private PrintStream dataOutputStream;
    private OnMessageReceivedListener messageListener;

    private final String chattingWithUsername;
    private final String username;
    private final String profilePicturePath;

    public ChatClient(String chattingWithUsername, String profilePicturePath, String username) throws IOException {
        String serverHost = "localhost";
        int controlPort = 12345;
        int dataPort = 8080;

        this.controlSocket = new Socket();
        this.controlSocket.connect(new InetSocketAddress(serverHost, controlPort), 5000);
        this.controlScanner = new Scanner(controlSocket.getInputStream());
        this.controlOutputStream = new PrintStream(controlSocket.getOutputStream());


        this.dataSocket = new Socket();
        this.dataSocket.connect(new InetSocketAddress(serverHost, dataPort), 5000);
        this.dataOutputStream = new PrintStream(dataSocket.getOutputStream());

        this.chattingWithUsername = chattingWithUsername;
        this.profilePicturePath = profilePicturePath;
        this.username = username;
    }

    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        this.messageListener = listener;
    }

    public void sendMessage(String message) {
        if (controlOutputStream != null) {
            JSONObject json = new JSONObject();
            json.put("type", "text");
            json.put("message", message);
            json.put("username", username);
            json.put("chattingWithUsername", chattingWithUsername);
            controlOutputStream.println(json.toJSONString());
            controlOutputStream.flush();
        }
    }

    public void sendImage(File image) {
        if (dataOutputStream != null) {
            try {
                System.out.println("[DEBUG] Preparing to send image: " + image.getAbsolutePath());

                if (image == null || !image.exists()) {
                    System.err.println("[ERROR] Image file does not exist or is null.");
                    showAlert("Error", "Image file does not exist or is null.");
                    return;
                }


                BufferedImage bufferedImage = ImageIO.read(image);
                if (bufferedImage != null) {
                    ImageIO.write(bufferedImage, "png", dataOutputStream);
                    dataOutputStream.flush();
                    System.out.println("[DEBUG] Image sent successfully: " + image.getName());
                } else {
                    System.err.println("[ERROR] Failed to read the image file: " + image.getAbsolutePath());
                }
            } catch (IOException e) {
                showAlert("Error", "Failed to send image: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("[ERROR] Data output stream is null. Cannot send image.");
        }
    }




    public void sendImageFile(String filepath)
    {
        if (controlOutputStream != null) {
            JSONObject json = new JSONObject();
            json.put("type", "filename");
            json.put("filename",filepath);
            json.put("username", username);
            json.put("chattingWithUsername", chattingWithUsername);
            controlOutputStream.println(json.toJSONString());
            controlOutputStream.flush();
        }

    }

    public void startListening() {
        new Thread(() -> {
            try {
                while (controlScanner.hasNextLine()) {
                    String rawMessage = controlScanner.nextLine();
                    System.out.println(rawMessage);
                    try {
                        JSONObject jsonMessage = (JSONObject) new JSONParser().parse(rawMessage);
                        String type = (String) jsonMessage.get("type");
                        switch (type) {
                            case "text":
                                handleTextMessage(jsonMessage);
                                break;
                            case "command":
                                handleCommandMessage(jsonMessage);
                                break;
                            case "filename":
                                handleFileName(jsonMessage);
                                break;
                            default:
                                System.out.println("[DEBUG] Unknown message type received: " + type);
                        }
                    } catch (ParseException e) {
                        Platform.runLater(() -> showAlert("Error", "Failed to parse message: " + rawMessage));
                    }
                }
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Error", "Connection lost: " + e.getMessage()));
            }
        }).start();
    }

    private void handleCommandMessage(JSONObject jsonMessage) {
        String filename = (String) jsonMessage.get("filename");
        String time = new SimpleDateFormat("h:mm a").format(new Date());

        sendImageFile(filename);
        String notification = "File sent: " + filename + " at " + time;
    }




    private void handleTextMessage(JSONObject jsonMessage) {
        String username = (String) jsonMessage.get("username");
        String chattingWithUsername = (String) jsonMessage.get("chattingWithUsername");
        String message = (String) jsonMessage.get("message");
        String time = (String) jsonMessage.get("time");

        String formattedMessage = chattingWithUsername + ": " + time + ": " + username + ": " + message;
        System.out.println(formattedMessage);
        Platform.runLater(() -> messageListener.onMessageReceived(formattedMessage));
    }

    private void handleFileName(JSONObject jsonMessage) {
        String username = (String) jsonMessage.get("username");
        String chattingWithUsername = (String) jsonMessage.get("chattingWithUsername");
        String filename = (String) jsonMessage.get("filename");
        String time = (String) jsonMessage.get("time");

        String formattedMessage = "Image: "+ chattingWithUsername + ": " + time + ": " + username + ": " + filename;
        System.out.println(formattedMessage);
        Platform.runLater(() -> messageListener.onMessageReceived(formattedMessage));
    }

    public void close() {
        try {
            if (controlSocket != null) controlSocket.close();
            if (dataSocket != null) dataSocket.close();
            if (controlScanner != null) controlScanner.close();
            if (controlOutputStream != null) controlOutputStream.close();
            if (dataOutputStream != null) dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
    }

    public String getChattingWithUsername() {
        return chattingWithUsername;
    }

    public String getUsername() {
        return username;
    }

    public String getImageFile() {
        return profilePicturePath;
    }

}
