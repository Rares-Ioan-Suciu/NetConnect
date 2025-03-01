package com.netconnect.Applications.UserApp.UserScenes.MenusLogin;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess.UpdateProfileScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;

public class MainScreenScene implements Sceneable {

    private final SceneNavigator navigator;

    public MainScreenScene(SceneNavigator navigator) {
        this.navigator = navigator;

    }

    @Override
    public Scene getScene() {

        Label title = new Label("Welcome to NetConnect");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;");


        VBox centerLayout = new VBox(20);
        centerLayout.setPadding(new Insets(30));
        centerLayout.setAlignment(Pos.CENTER);


        Button matchButton = createStyledButton("Match", "#0073e6", "#005bb5");
        matchButton.setOnAction(event -> navigator.navigateTo(MatchScene.class));

        Button seeProfileButton = createStyledButton("See Profile", "#4CAF50", "#388E3C");
        seeProfileButton.setOnAction(event -> navigator.navigateTo(SeeProfileScene.class));

        Button updateButton = createStyledButton("Update Profile", "#0073e6", "#005bb5");
        updateButton.setOnAction(event -> navigator.navigateTo(UpdateProfileScene.class));

        Button reportBugButton = createStyledButton("Report Bug", "#FFC300", "#DAA520");
        reportBugButton.setOnAction(event -> navigator.navigateTo(ReportBugScene.class));

        centerLayout.getChildren().addAll(title, matchButton, seeProfileButton, updateButton, reportBugButton);

        Button exitButton = createStyledButton("Exit", "#FF5733", "#C0392B");
        exitButton.setOnAction(event -> navigator.navigateTo(ExitScene.class));

        HBox bottomLayout = new HBox(exitButton);
        bottomLayout.setAlignment(Pos.CENTER);
        bottomLayout.setPadding(new Insets(10));

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(centerLayout);
        mainPane.setBottom(bottomLayout);
        mainPane.setStyle("-fx-background-color: #F5F5F5;");

        return new Scene(mainPane, 900, 1000);
    }

    private Button createStyledButton(String text, String bgColor, String hoverColor) {
        Button button = new Button(text);
        button.setStyle(String.format("-fx-font-size: 16px; -fx-background-color: %s; -fx-text-fill: white;", bgColor));
        button.setOnMouseEntered(e -> button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white;", hoverColor)));
        button.setOnMouseExited(e -> button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white;", bgColor)));
        return button;
    }
}
