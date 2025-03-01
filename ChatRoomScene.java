package com.netconnect.ChatRoom;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChatRoomScene implements Sceneable {
    private final SceneNavigator sceneNavigator;
    private final ChatClient chatClient;
    private final String chattingWithUsername;
    private final String chattingWithProfilePictureUrl;
    private final ConversationHandler conversationManager = new ConversationHandler();
    private final VBox chatArea;
    private final ScrollPane scrollPane;

    public ChatRoomScene(SceneNavigator sceneNavigator, ChatClient chatClient) {
        this.sceneNavigator = sceneNavigator;
        this.chatClient = chatClient;
        this.chattingWithUsername = chatClient.getChattingWithUsername();
        this.chattingWithProfilePictureUrl = chatClient.getImageFile();

        this.chatArea = new VBox(5);
        this.chatArea.setPadding(new Insets(10));
        this.chatArea.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd;");
        this.chatArea.setPrefHeight(500);


        this.scrollPane = new ScrollPane(chatArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        List<ConversationHandler.Message> previousMessages = conversationManager.loadConversation(
                sceneNavigator.currentUser.getUsername().getDetails(),
                chattingWithUsername
        );
        for (ConversationHandler.Message message : previousMessages) {
            if (message.isImage()) {
                appendImageToChatArea(message.getSender(), message.getMessage());
            } else {
                appendMessageToChatArea(message.getTimestamp(), message.getSender(), message.getMessage());
            }
        }

        chatClient.sendMessage(sceneNavigator.currentUser.getUsername().getDetails() + " " + chatClient.getChattingWithUsername());
    }


    @Override
    public Scene getScene() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f7fa, #c3cfe2);");

        HBox header = createHeader();
        HBox footer = createFooter();

        root.getChildren().addAll(header, scrollPane, footer);

        chatClient.setOnMessageReceivedListener(message -> {
            Platform.runLater(() -> {
                if (message.startsWith("Image:")) {
                    handleImageMessage(message);
                } else {
                    handleTextMessage(message);
                }
            });
        });

        chatClient.startListening();

        return new Scene(root, 900, 1000);
    }


    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #37474f; -fx-padding: 10px;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(event -> sceneNavigator.goBack());

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        exitButton.setOnAction(event -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });

        ImageView profilePicture = new ImageView();
        try {
            profilePicture.setImage(new Image(chattingWithProfilePictureUrl));
        } catch (Exception e) {
            System.err.println("[DEBUG] Failed to load profile picture, using default.");
            profilePicture.setImage(new Image("default_profile_picture.png"));
        }
        profilePicture.setFitWidth(50);
        profilePicture.setFitHeight(50);

        Text usernameText = new Text(chattingWithUsername);
        usernameText.setStyle("-fx-font-size: 20px; -fx-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(backButton, profilePicture, usernameText, spacer, exitButton);
        return header;
    }

    private HBox createFooter() {
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));
        footer.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #dddddd;");

        TextField messageInput = new TextField();
        messageInput.setPromptText("Type a message...");
        messageInput.setPrefWidth(350);

        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 14px;");
        sendButton.setOnAction(event -> handleMessageSend(messageInput));

        Button imageButton = new Button("Send Image");
        imageButton.setStyle("-fx-background-color: #03a9f4; -fx-text-fill: white; -fx-font-size: 14px;");
        imageButton.setOnAction(event -> handleImageSend());

        messageInput.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                handleMessageSend(messageInput);
            }
        });

        footer.getChildren().addAll(messageInput, sendButton, imageButton);
        return footer;
    }

    private void handleMessageSend(TextField messageInput) {
        String message = messageInput.getText().trim();
        if (isMessageValid(message)) {
            String time = new SimpleDateFormat("h:mm a").format(new Date());
            conversationManager.saveMessage(sceneNavigator.currentUser.getUsername().getDetails(), chattingWithUsername, message, time, false);
            chatClient.sendMessage(message);
            appendMessageToChatArea(time, sceneNavigator.currentUser.getUsername().getDetails(), message);
            messageInput.clear();
        } else {
            showValidationAlert();
        }
    }

    private void handleImageSend() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            String time = new SimpleDateFormat("h:mm a").format(new Date());
            chatClient.sendImage(selectedFile);
            conversationManager.saveMessage(sceneNavigator.currentUser.getUsername().getDetails(), chattingWithUsername, selectedFile.getAbsolutePath(), time, true);
            appendImageToChatArea(sceneNavigator.currentUser.getUsername().getDetails(), selectedFile.getAbsolutePath());
        }
    }

    private void handleTextMessage(String message) {
        String[] parts = message.split(": ", 4);
        if (parts.length < 4) {
            System.err.println("[DEBUG] Invalid message format: " + message);
            return;
        }
        String time = parts[1];
        String sender = parts[2];
        String content = parts[3];
        appendMessageToChatArea(time, sender, content);
    }

    private void handleImageMessage(String message) {
        String[] parts = message.split(": ", 5);
        System.out.println(Arrays.toString(parts));
        if (parts.length < 5) {
            System.err.println("[DEBUG] Invalid image message format: " + message);
            return;
        }
        String sender = parts[3];
        String imagePath = parts[4];
        System.out.println(imagePath);
        appendImageToChatArea(sender, imagePath);
    }

    private void appendMessageToChatArea(String time, String sender, String message) {
        Text text = new Text(String.format("[%s] %s: %s", time, sender, message));
        text.setStyle("-fx-font-family: Arial; -fx-font-size: 14px;");
        chatArea.getChildren().add(text);
    }

    private void appendImageToChatArea(String sender, String imageUrl) {
        HBox imageMessageBox = new HBox(10);
        imageMessageBox.setAlignment(Pos.CENTER_LEFT);
        imageMessageBox.setPadding(new Insets(5));

        Text senderText = new Text(sender + ": ");
        senderText.setStyle("-fx-font-family: Arial; -fx-font-size: 14px; -fx-font-weight: bold;");

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image("file:" + imageUrl));
        } catch (Exception e) {
            System.err.println("[DEBUG] Failed to load image: " + imageUrl);
            return;
        }
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);

        imageMessageBox.getChildren().addAll(senderText, imageView);
        chatArea.getChildren().add(imageMessageBox);
    }

    private boolean isMessageValid(String message) {
        return !message.isEmpty() && message.length() <= 500;
    }

    private void showValidationAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Message");
        alert.setHeaderText(null);
        alert.setContentText("Message must be between 1 and 500 characters.");
        alert.showAndWait();
    }
}
