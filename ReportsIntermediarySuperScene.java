package com.netconnect.Applications.SuperAdminApp.Scenes;

import com.netconnect.Applications.SuperAdminApp.Helpers.SuperAdminNavigator;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ReportsIntermediarySuperScene implements Sceneable {

    private final SuperAdminNavigator navigator;

    public ReportsIntermediarySuperScene(SuperAdminNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public Scene getScene() {
        Label reportsLabel = new Label("Super Admin Reports");
        reportsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #283593;");

        Button bugReportsButton = new Button("Bug Reports");
        bugReportsButton.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Button profileReportsButton = new Button("Profile Reports");
        profileReportsButton.setStyle("-fx-background-color: #1e88e5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #c84b31; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(event -> navigator.goBack());

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        bugReportsButton.setOnAction(event -> navigator.navigateTo(ReportsSuperBugsScene.class));
        profileReportsButton.setOnAction(event -> navigator.navigateTo(ReportSuperProfilesScene.class));
        exitButton.setOnAction(event -> navigator.close());

        VBox layout = new VBox(20, reportsLabel, bugReportsButton, profileReportsButton, backButton, exitButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 30; -fx-background-color: #e3f2fd; -fx-border-color: #90caf9; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        return new Scene(layout, 700, 800);
    }
}
