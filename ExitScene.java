package com.netconnect.Applications.UserApp.UserScenes.MenusLogin;

import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ExitScene implements Sceneable {

    private final SceneNavigator navigator;

    public ExitScene(SceneNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public Scene getScene() {

        Label exitLabel = new Label("Are you sure you want to exit?");
        exitLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2F855A;");


        Button confirmExitButton = new Button("Yes");
        confirmExitButton.setId("confirmExitButton");
        confirmExitButton.setStyle("-fx-background-color: #E53E3E; -fx-text-fill: white; -fx-font-size: 14px;");
        confirmExitButton.setOnAction(event -> {
            navigator.close();
        });

        Button cancelExitButton = new Button("No");
        cancelExitButton.setStyle("-fx-background-color: #2F855A; -fx-text-fill: white; -fx-font-size: 14px;");
        cancelExitButton.setOnAction(event -> navigator.goBack());
        cancelExitButton.setId("cancelExitButton");

        Button reportBugButton = new Button("Report Bug");
        reportBugButton.setStyle("-fx-background-color: #68D391; -fx-text-fill: white; -fx-font-size: 14px;");
        reportBugButton.setOnAction(actionEvent -> navigator.navigateTo(ReportBugScene.class));
        reportBugButton.setId("reportBugButton");


        HBox buttonBox = new HBox(15, confirmExitButton, cancelExitButton, reportBugButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, exitLabel, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F0FFF4;");

        return new Scene(layout, 400, 400);
    }
}
