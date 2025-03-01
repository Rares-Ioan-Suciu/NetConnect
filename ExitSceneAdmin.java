package com.netconnect.Applications.AdminApp.AdminUI;

import com.netconnect.Applications.AdminApp.AdminApplication;
import com.netconnect.Applications.AdminApp.Helpers.AdminSceneNavigator;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ExitSceneAdmin implements Sceneable {

    private final AdminSceneNavigator navigator;

    public ExitSceneAdmin(AdminSceneNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public Scene getScene() {

        Label exitLabel = new Label("Are you sure you want to exit?");
        exitLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        Button confirmExitButton = new Button("Yes");
        confirmExitButton.setStyle("-fx-background-color: #c84b31; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        confirmExitButton.setOnAction(event -> {
            System.out.println("Exiting the application. Goodbye!");
            navigator.close();
        });

        Button cancelExitButton = new Button("No");
        cancelExitButton.setStyle("-fx-background-color: #4a6fa5; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        cancelExitButton.setOnAction(event -> navigator.goBack());

        HBox buttonBox = new HBox(15, confirmExitButton, cancelExitButton);
        buttonBox.setAlignment(Pos.CENTER);


        VBox layout = new VBox(20, exitLabel, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9; -fx-border-color: #8a94b8; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        return new Scene(layout, 400, 200);
    }
}
