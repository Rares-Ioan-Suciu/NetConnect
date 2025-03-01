package com.netconnect.ChatRoom;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConversationHandler {
    private static final String STORAGE_DIRECTORY = "conversations";

    static class Message {
        private String sender;
        private String receiver;
        private String message;
        private String timestamp;
        private boolean isImage;

        public Message() {
        }

        public Message(String sender, String receiver, String message, String timestamp, boolean isImage) {
            this.sender = sender;
            this.receiver = receiver;
            this.message = message;
            this.timestamp = timestamp;
            this.isImage = isImage;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public boolean isImage() {
            return isImage;
        }

        public void setImage(boolean image) {
            isImage = image;
        }
    }

    public ConversationHandler() {
        File directory = new File(STORAGE_DIRECTORY);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("[INFO] Created storage directory: " + STORAGE_DIRECTORY);
            } else {
                System.err.println("[ERROR] Failed to create storage directory: " + STORAGE_DIRECTORY);
            }
        }
    }

    public void saveMessage(String sender, String receiver, String messageContent, String timestamp, boolean isImage) {
        if (isInvalidMessage(sender, receiver, messageContent)) {

            return;
        }

        String fileName = getConversationFileName(sender, receiver);
        File file = new File(STORAGE_DIRECTORY, fileName);

        ObjectMapper mapper = new ObjectMapper();
        List<Message> conversation;
       if (file.exists()) {
            try {
                conversation = mapper.readValue(
                        file,
                        mapper.getTypeFactory().constructCollectionType(List.class, Message.class)
                );
            } catch (IOException e) {

                conversation = new ArrayList<>();
            }
        } else {
            conversation = new ArrayList<>();
        }

        conversation.add(new Message(sender, receiver, messageContent, timestamp, isImage));

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, conversation);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public List<Message> loadConversation(String userA, String userB) {
        String fileName = getConversationFileName(userA, userB);
        File file = new File(STORAGE_DIRECTORY, fileName);

        if (file.exists()) {
            if (file.length() > 0) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(
                            file,
                            mapper.getTypeFactory().constructCollectionType(List.class, Message.class)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new ArrayList<>();
    }

    private String getConversationFileName(String userA, String userB) {
        String sortedUsers = userA.compareTo(userB) < 0 ? userA + "_" + userB : userB + "_" + userA;
        return sortedUsers + ".json";
    }

    private boolean isInvalidMessage(String sender, String receiver, String messageContent) {
        return sender == null || sender.trim().isEmpty() ||
                receiver == null || receiver.trim().isEmpty() ||
                messageContent == null || messageContent.trim().isEmpty();
    }
}
