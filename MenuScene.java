package com.netconnect.Applications.UserApp.UserScenes.MenusLogin;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess.SignUpScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;

public class MenuScene implements Sceneable {

    private final SceneNavigator navigator;

    public MenuScene(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    public Scene getScene() {

        Label title = new Label("Welcome to NetConnect");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #004d00;");


        Button loginButton = new Button("Login");
        Button signUpButton = new Button("Sign Up");
        Button exitButton = new Button("Exit");
        Button reportBug = new Button("Report Bug");


        loginButton.setOnAction(event -> navigator.navigateTo(LoginScene.class));
        signUpButton.setOnAction(event -> navigator.navigateTo(SignUpScene.class));
        exitButton.setOnAction(event -> navigator.navigateTo(ExitScene.class));
        reportBug.setOnAction(event -> navigator.navigateTo(ReportBugScene.class));

        String defaultButtonStyle = "-fx-font-size: 14px; -fx-min-width: 150px; -fx-background-color: #66cc66; -fx-text-fill: white;";
        String hoverButtonStyle = "-fx-background-color: #33aa33; -fx-text-fill: white;";
        String exitButtonStyle = "-fx-font-size: 14px; -fx-min-width: 150px; -fx-background-color: #cc6666; -fx-text-fill: white;";
        String exitHoverStyle = "-fx-background-color: #aa3333; -fx-text-fill: white;";

        loginButton.setStyle(defaultButtonStyle);
        signUpButton.setStyle(defaultButtonStyle);
        reportBug.setStyle(defaultButtonStyle);
        exitButton.setStyle(exitButtonStyle);

        loginButton.setOnMouseEntered(e -> loginButton.setStyle(hoverButtonStyle));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(defaultButtonStyle));
        signUpButton.setOnMouseEntered(e -> signUpButton.setStyle(hoverButtonStyle));
        signUpButton.setOnMouseExited(e -> signUpButton.setStyle(defaultButtonStyle));
        reportBug.setOnMouseEntered(e -> reportBug.setStyle(hoverButtonStyle));
        reportBug.setOnMouseExited(e -> reportBug.setStyle(defaultButtonStyle));
        exitButton.setOnMouseEntered(e -> exitButton.setStyle(exitHoverStyle));
        exitButton.setOnMouseExited(e -> exitButton.setStyle(exitButtonStyle));

        VBox buttonBox = new VBox(15, loginButton, signUpButton, reportBug);
        buttonBox.setAlignment(Pos.CENTER);

        HBox exitBox = new HBox(exitButton);
        exitBox.setAlignment(Pos.CENTER);
        exitBox.setPadding(new Insets(20, 0, 0, 0));

        VBox layout = new VBox(20, title, buttonBox, exitBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        layout.setStyle("-fx-background-color: #e6ffe6;");

        return new Scene(layout, 900, 1000);
    }
}
