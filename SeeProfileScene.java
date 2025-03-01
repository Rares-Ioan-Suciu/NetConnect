package com.netconnect.Applications.UserApp.UserScenes.MenusLogin;

import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.User;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.ChatRoom.ChatClient;
import com.netconnect.ChatRoom.ChatRoomScene;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SeeProfileScene implements Sceneable {

    private final SceneNavigator sceneNavigator;
    private final User currentUser;
    private final Boolean isDifferent;
    private final List<User> matches;
    private int currentMatchIndex;

    public SeeProfileScene(SceneNavigator sceneNavigator, User currentUser, Boolean isDifferent, int startingIndex) {
        this.sceneNavigator = sceneNavigator;
        this.currentUser = currentUser;
        this.isDifferent = isDifferent;
        this.matches = currentUser.getBestMatches();
        this.currentMatchIndex = startingIndex;
    }

    public Scene getScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 20px;");

        BorderPane layout = new BorderPane();

        User match = isDifferent ? matches.get(currentMatchIndex) : currentUser;

        ImageView profilePictureView = loadProfilePicture(match);
        StackPane profilePicturePane = new StackPane(profilePictureView);
        profilePicturePane.setAlignment(Pos.CENTER);
        layout.setTop(profilePicturePane);

        Label profileInfoLabel = new Label(generateUserInfo(match));
        profileInfoLabel.setFont(Font.font("Arial", 16));
        profileInfoLabel.setTextFill(Color.DARKGRAY);
        profileInfoLabel.setWrapText(true);
        VBox infoBox = new VBox(10, profileInfoLabel);
        infoBox.setAlignment(Pos.CENTER);
        layout.setCenter(infoBox);

        HBox buttonBox = createButtonBox(profileInfoLabel, profilePictureView);
        layout.setBottom(buttonBox);

        root.getChildren().add(layout);
        return new Scene(root, 900, 1000);
    }

    private HBox createButtonBox(Label profileInfoLabel, ImageView profilePictureView) {
        Button backButton = new Button("Back");
        styleButton(backButton, "#6c757d");
        backButton.setOnAction(event -> sceneNavigator.navigateTo(MainScreenScene.class));

        HBox buttonBox = new HBox(10, backButton);

        if (isDifferent) {
            Button reportBugButton = new Button("Report Bug");
            styleButton(reportBugButton, "#28a745");
            reportBugButton.setOnAction(event -> sceneNavigator.navigateTo(ReportBugScene.class));

            Button chatWithUserButton = new Button("Chat with User");
            styleButton(chatWithUserButton, "#6c757d");
            chatWithUserButton.setOnAction(event -> {
                try {
                    ChatClient chatClient = new ChatClient(matches.get(currentMatchIndex).getUsername().getDetails(), getImagePath(matches.get(currentMatchIndex)), currentUser.getUsername().getDetails());
                    sceneNavigator.setChatClient(chatClient);
                    sceneNavigator.navigateTo(ChatRoomScene.class);
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(e.getMessage());
                    alert.showAndWait();
                }
            });

            Button reportProfileButton = new Button("Report Profile");
            styleButton(reportProfileButton, "#dc3545");
            reportProfileButton.setOnAction(event -> sceneNavigator.navigateTo(ReportProfileScene.class));

            Button nextButton = new Button("Next");
            styleButton(nextButton, "#007bff");
            nextButton.setOnAction(event -> {
                currentMatchIndex = (currentMatchIndex + 1) % matches.size();
                User match = matches.get(currentMatchIndex);
                profileInfoLabel.setText(generateUserInfo(match));
                profilePictureView.setImage(new Image(getImagePath(match)));
            });

            buttonBox.getChildren().addAll(reportBugButton, chatWithUserButton, reportProfileButton, nextButton);
        }

        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }


    private String generateUserInfo(User user) {
        StringBuilder userInfo = new StringBuilder();
        userInfo.append("Username: ").append(user.getUsername().getDetails()).append("\n");
        userInfo.append("Email: ").append(user.getEmail().getDetails()).append("\n");

        if (user.getNameDetails() != null) {
            userInfo.append("Full Name: ").append(user.getNameDetails().getDetails()).append("\n");
        }
        if (user.getGenderDetails() != null) {
            userInfo.append("Gender: ").append(user.getGenderDetails()).append("\n");
        }
        if (user.getDateOfBirthDetails() != null) {
            userInfo.append("Date of Birth: ").append(user.getDateOfBirthDetails().getDetails()).append("\n");
        }

        appendListInfo(userInfo, "Interests", user.getInterests());
        appendListInfo(userInfo, "Abilities", user.getAbilities());
        appendListInfo(userInfo, "Languages", user.getLanguages());
        appendListInfo(userInfo, "Expertise", user.getExpertise());
        appendListInfo(userInfo, "Education", user.getEducation());

        if (user.getQuestion() != null) {
            userInfo.append("Question: ").append(user.getQuestion().getText()).append("\n");
        }

        return userInfo.toString();
    }

    private void appendListInfo(StringBuilder builder, String label, List<String> items) {
        builder.append(label).append(": ");
        if (items == null || items.isEmpty()) {
            builder.append("Not provided\n");
        } else {
            builder.append(String.join(", ", items)).append("\n");
        }
    }

    private String getImagePath(User user) {
        if(user.getimagePath()!=null)
        {
            File file = new File(user.getimagePath());
            return file.toURI().toString();
        }
        File profilePictureFilePng = new File("Users/" + user.getUsername().getDetails() + "_profile_picture.png");
        File profilePictureFileJpg = new File("Users/" + user.getUsername().getDetails() + "_profile_picture.jpg");
        File profilePictureFileJpeg = new File("Users/" + user.getUsername().getDetails() + "_profile_picture.jpeg");

        File profilePictureFile = new File("Users/default_profile_picture.png");
        if (profilePictureFilePng.exists()) {
            profilePictureFile = profilePictureFilePng;
        } else if (profilePictureFileJpg.exists()) {
            profilePictureFile = profilePictureFileJpg;
        } else if (profilePictureFileJpeg.exists()) {
            profilePictureFile = profilePictureFileJpeg;
        }

        return profilePictureFile.toURI().toString();
    }

    private ImageView loadProfilePicture(User user) {
        Image profileImage = new Image(getImagePath(user));
        ImageView profilePictureView = new ImageView(profileImage);
        profilePictureView.setFitWidth(500);
        profilePictureView.setFitHeight(500);
        profilePictureView.setStyle("-fx-border-color: #333; -fx-border-width: 3px;");
        return profilePictureView;
    }

    private void styleButton(Button button, String color) {
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-background-radius: 8px;");
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + darkenColor(color) + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-background-radius: 8px;"));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-background-radius: 8px;"));
    }

    private String darkenColor(String color) {
        switch (color) {
            case "#28a745":
                return "#218838";
            case "#6c757d":
                return "#5a6268";
            case "#dc3545":
                return "#c82333";
            case "#007bff":
                return "#0056b3";
            default:
                return color;
        }
    }
}
