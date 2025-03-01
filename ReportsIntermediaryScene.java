package com.netconnect.Applications.AdminApp.AdminUI;

import com.netconnect.Applications.AdminApp.Helpers.AdminSceneNavigator;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ReportsIntermediaryScene implements Sceneable {

    private final AdminSceneNavigator navigator;

    public ReportsIntermediaryScene(AdminSceneNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public Scene getScene() {

        Label reportsLabel = new Label("Reports");
        reportsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2f4f2f;"); // Dark green color


        Button reportsBugs = new Button("Bug Reports");
        reportsBugs.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Button reportsProfile = new Button("Profile Reports");
        reportsProfile.setStyle("-fx-background-color: #8bc34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");


        reportsBugs.setOnAction(event -> navigator.navigateTo(ReportsBugsScene.class));
        reportsProfile.setOnAction(event -> navigator.navigateTo(ReportsProfilesScene.class));
        exitButton.setOnAction(event -> navigator.navigateTo(ExitSceneAdmin.class));

        VBox layout = new VBox(20, reportsLabel, reportsBugs, reportsProfile, exitButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 30; -fx-background-color: #e8f5e9; -fx-border-color: #66bb6a; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        return new Scene(layout, 800, 900);
    }
}
