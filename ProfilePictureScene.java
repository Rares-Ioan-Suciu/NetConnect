package com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess;

import com.netconnect.Applications.UserApp.User;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.UserScenes.MenusLogin.MainScreenScene;
import com.netconnect.Applications.UserApp.UserScenes.MenusLogin.ReportBugScene;
import com.netconnect.QueryExecutor;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.UserApplication;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfilePictureScene implements Sceneable {

    private final SceneNavigator navigator;
    private final UserApplication userApp;
    private final User currentUser;

    public ProfilePictureScene(SceneNavigator navigator, UserApplication userApp, User currentUser) {
        this.navigator = navigator;
        this.userApp = userApp;
        this.currentUser = currentUser;
    }

    public Scene getScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #e8f5e9; -fx-padding: 20px;");

        Label instructionLabel = new Label("Please select a profile picture:");
        instructionLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #2e7d32;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(300);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);");

        Button uploadButton = new Button("Upload Profile Picture");
        uploadButton.setStyle("-fx-background-color: #66bb6a; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-border-color: #2e7d32; -fx-border-width: 2px;");
        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(navigator.getStage());


            if (file != null) {
                try {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    userApp.updateProfilePicture(file, currentUser.getUsername().getDetails());
                    String profilePicturePath = "Users/" + currentUser.getUsername().getDetails() + "_profile_picture" + getFileExtension(file.getName());
                    currentUser.setimagePath(profilePicturePath);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Invalid file format or corrupted image.");
                } catch (ArrayIndexOutOfBoundsException arrEx) {
                    System.out.println("Error accessing user details. Please check user profile information.");
                }
            } else {
                System.out.println("No file selected.");
            }
        });

        Button nextButton = new Button("Next/Skip");
        nextButton.setStyle("-fx-background-color: #43a047; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-border-color: #1b5e20; -fx-border-width: 2px;");
        nextButton.setOnAction(e -> {
            if(!currentUser.getUpdating()) {
                setFinished();
                navigator.navigateTo(MainScreenScene.class);
            }
            else
            {


                navigator.goBack();
            }
        });

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #81c784; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-border-color: #388e3c; -fx-border-width: 2px;");
        backButton.setOnAction(event -> navigator.goBack());

        Button reportBug = new Button("Report Bug");
        reportBug.setStyle("-fx-background-color: #8bc34a; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-border-color: #558b2f; -fx-border-width: 2px;");
        reportBug.setOnAction(actionEvent -> navigator.navigateTo(ReportBugScene.class));

        HBox buttonBox = new HBox(10, backButton, reportBug, nextButton);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(instructionLabel, imageView, uploadButton, buttonBox);
        return new Scene(layout, 900, 1000);
    }

    private void setFinished()
    {
        String username = currentUser.getUsername().getDetails();
        final QueryExecutor queryExecutor = new QueryExecutor();
        final String updateFinished = "UPDATE user_details SET is_finished = 1 where username = ?;";

        try(Connection connection = queryExecutor.getAppUserConnection(); PreparedStatement isFinished = connection.prepareStatement(updateFinished)) {
            isFinished.setString(1, username);
            isFinished.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
}
