package com.netconnect.Applications.SuperAdminApp.Scenes;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.SuperAdminApp.Helpers.SuperAdminNavigator;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;


public class SuperAdminOptionsScene implements Sceneable {


    private final SuperAdminNavigator navigator;

    public SuperAdminOptionsScene(SuperAdminNavigator superAdminNavigator) {
        this.navigator = superAdminNavigator;
    }

    @Override
    public Scene getScene() {
        Button reportsButton = new Button("See reports");
        reportsButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Button adminButton = new Button("Manage admins");
        adminButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Button usersButton = new Button("Manage users");
        usersButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");


        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #c84b31; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        reportsButton.setOnAction(event -> navigator.navigateTo(ReportsIntermediarySuperScene.class));
        adminButton.setOnAction(event -> navigator.navigateTo(ManageAdminsScene.class));
        usersButton.setOnAction(event -> navigator.navigateTo(ManageUsersScene.class));
        exitButton.setOnAction(event -> navigator.close());


        VBox layout = new VBox(20, reportsButton, adminButton, usersButton, exitButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 30; -fx-background-color: #f9f9f9; -fx-border-color: #8a94b8; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        return new Scene(layout, 700, 800);

    }

}
