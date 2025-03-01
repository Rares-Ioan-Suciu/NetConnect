package com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.User;
import com.netconnect.Applications.UserApp.UserScenes.MenusLogin.ExitScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UpdateProfileScene implements Sceneable {

    private final SceneNavigator navigator;
    private final User user;

    public UpdateProfileScene(SceneNavigator navigator, User user) {
        this.navigator = navigator;
        this.user = user;
        user.setUpdating(true);
    }

    @Override
    public Scene getScene() {
        Label title = new Label("NetConnect");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;");

        VBox centerLayout = new VBox(15);
        centerLayout.setPadding(new Insets(30));
        centerLayout.setAlignment(Pos.CENTER);

        Button changePassword = createStyledButton("Change Password", 0);
        changePassword.setOnAction(event -> navigator.navigateTo(ChangePasswordScene.class));

        Button changeProfilePicture = createStyledButton("Change Profile Picture", 1);
        changeProfilePicture.setOnAction(event -> navigator.navigateTo(ProfilePictureScene.class));

        Button updateInterests = createStyledButton("Update Interests", 2);
        updateInterests.setOnAction(event -> navigator.navigateTo(UserGeneralDetailsScene.class));

        Button updateDateOfBirth = createStyledButton("Update Date of Birth", 3);
        updateDateOfBirth.setOnAction(event -> navigator.navigateTo(ChangeDOBScene.class));

        Button updateGender = createStyledButton("Update Gender", 4);
        updateGender.setOnAction(event -> navigator.navigateTo(ChangeGenderScene.class));

        Button backButton = createStyledButton("Back", 5);
        backButton.setOnAction(event -> {
            user.setUpdating(false);
            navigator.goBack();
        });

        centerLayout.getChildren().addAll(
                title,
                changePassword,
                changeProfilePicture,
                updateInterests,
                updateDateOfBirth,
                updateGender,
                backButton
        );

        Button exitButton = createStyledButton("Exit", 6);
        exitButton.setStyle("-fx-background-color: #FF4C4C; -fx-text-fill: white;");
        exitButton.setOnAction(event -> {
            user.setUpdating(false);
            navigator.navigateTo(ExitScene.class);
        });

        HBox bottomLayout = new HBox(exitButton);
        bottomLayout.setAlignment(Pos.CENTER);
        bottomLayout.setPadding(new Insets(10));

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(centerLayout);
        mainPane.setBottom(bottomLayout);
        mainPane.setStyle("-fx-background-color: #FFFFFF;");

        return new Scene(mainPane, 900, 1000);
    }

    private Button createStyledButton(String text, int index) {
        Button button = new Button(text);
        String[] colors = {"#0078D7", "#1E90FF", "#4682B4"};
        String backgroundColor = colors[index % colors.length];
        button.setStyle("-fx-font-size: 16px; -fx-background-color: " + backgroundColor + "; -fx-text-fill: white;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #005A9E; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white;"));
        return button;
    }
}
