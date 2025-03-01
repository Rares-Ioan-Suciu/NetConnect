package com.netconnect.ChatRoom;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Receiver implements Runnable {

    private final ChatUser user;
    private final ChatServer server;

    public Receiver(ChatServer server, ChatUser user) {
        this.server = server;
        this.user = user;
    }

    @Override
    public void run() {
        try (InputStream inputStream = user.getControlInStream()) {
            Scanner scanner = new Scanner(inputStream);

            while (!user.isControlSocketClosed()) {
                if (scanner.hasNextLine()) {
                    String rawInput = scanner.nextLine();
                    handleRawInput(rawInput);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void handleRawInput(String rawInput) {
        try {
            JSONObject json = parseJson(rawInput);
            if (json == null) {
                return;
            }



            String type = (String) json.get("type");
            if (type == null) {

                return;
            }

            switch (type) {
                case "text":
                    handleTextMessage(json);
                    break;
                case "filename":
                    handleFileNameMessage(json);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void handleTextMessage(JSONObject json) {
        try {
            String message = (String) json.get("message");
            if (message == null) {

                return;
            }

            String timestamp = new SimpleDateFormat("h:mm a").format(new Date());

            JSONObject jsonReturn = new JSONObject();
            jsonReturn.put("type", "text");
            jsonReturn.put("username", user.getUsername());
            jsonReturn.put("chattingWithUsername", user.getChattingWithUsername());
            jsonReturn.put("message", message);
            jsonReturn.put("time", timestamp);


            broadcastToAllUsers(jsonReturn.toString());
        } catch (Exception e) {

        }
    }

    private void handleFileNameMessage(JSONObject json) {
        try {
            String filename = (String) json.get("filename");
            if (filename == null) {

                return;
            }

            String timestamp = new SimpleDateFormat("h:mm a").format(new Date());

            JSONObject jsonReturn = new JSONObject();
            jsonReturn.put("type", "filename");
            jsonReturn.put("username", user.getUsername());
            jsonReturn.put("chattingWithUsername", user.getChattingWithUsername());
            jsonReturn.put("filename", filename);
            jsonReturn.put("time", timestamp);


            broadcastToAllUsers(jsonReturn.toString());
        } catch (Exception e) {
            System.err.println("[ERROR] Error handling filename message: " + e.getMessage());
        }
    }

    private JSONObject parseJson(String rawJson) {
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(rawJson);
        } catch (ParseException e) {
            return null;
        }
    }

    private void broadcastToAllUsers(String message) {
        for (ChatUser thread : server.getAllUsers()) {
            if (!thread.equals(user)) {
                sendMessage(message, thread);
            }
        }
    }

    private void sendMessage(String message, ChatUser recipient) {
        try {
            PrintStream out = recipient.getControlOutStream();
            out.println(message);
            out.flush();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to send message to user " + recipient.getUsername() + ": " + e.getMessage());
        }
    }
}
