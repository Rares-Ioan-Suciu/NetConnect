package com.netconnect.Applications.SuperAdminApp.Scenes;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.SuperAdminApp.Helpers.SuperAdminNavigator;
import com.netconnect.Applications.SuperAdminApp.SuperAdminApp;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SuperAdminMenuScene implements Sceneable {

    private final SuperAdminNavigator navigator;

    public SuperAdminMenuScene(SuperAdminNavigator superAdminNavigator) {
        this.navigator = superAdminNavigator;
    }

    @Override
    public Scene getScene() {
        Button loginButton = new Button("SuperAdmin Login");
        loginButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #c84b31; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        loginButton.setOnAction(event -> navigator.navigateTo(SuperAdminLoginScene.class));
        exitButton.setOnAction(event -> navigator.close());

        VBox layout = new VBox(20, loginButton, exitButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 30; -fx-background-color: #f9f9f9; -fx-border-color: #8a94b8; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        return new Scene(layout, 700, 800);
    }
}
