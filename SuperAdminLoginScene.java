package com.netconnect.Applications.SuperAdminApp.Scenes;

import com.netconnect.Applications.SuperAdminApp.Helpers.SuperAdminNavigator;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.SuperAdminApp.SuperAdminApp;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


import java.util.regex.Pattern;

public class SuperAdminLoginScene implements Sceneable {
    private final SuperAdminNavigator navigator;
    private final SuperAdminApp superAdminApp;
    private boolean isPasswordVisible = false;
    private boolean isMasterKeyVisible = false;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    private static final Pattern MASTER_KEY_PATTERN = Pattern.compile("^.{8,}$");

    public SuperAdminLoginScene(SuperAdminNavigator navigator, SuperAdminApp superAdminApp) {
        this.superAdminApp = superAdminApp;
        this.navigator = navigator;
    }

    @Override
    public Scene getScene() {
        Text title = new Text("Super Admin Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setFill(Color.DARKRED);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #8a94b8; -fx-border-radius: 5;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #8a94b8; -fx-border-radius: 5;");

        TextField passwordVisibleField = new TextField();
        passwordVisibleField.setPromptText("Password");
        passwordVisibleField.setManaged(false);
        passwordVisibleField.setVisible(false);

        PasswordField masterKeyField = new PasswordField();
        masterKeyField.setPromptText("Master Key");
        masterKeyField.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #8a94b8; -fx-border-radius: 5;");

        TextField masterKeyVisibleField = new TextField();
        masterKeyVisibleField.setPromptText("Master Key");
        masterKeyVisibleField.setManaged(false);
        masterKeyVisibleField.setVisible(false);

        Button togglePasswordVisibilityButton = new Button("Show");
        togglePasswordVisibilityButton.setStyle("-fx-background-color: #dddddd;");
        togglePasswordVisibilityButton.setOnAction(e -> toggleVisibility(passwordField, passwordVisibleField, togglePasswordVisibilityButton));

        Button toggleMasterKeyVisibilityButton = new Button("Show");
        toggleMasterKeyVisibilityButton.setStyle("-fx-background-color: #dddddd;");
        toggleMasterKeyVisibilityButton.setOnAction(e -> toggleVisibility(masterKeyField, masterKeyVisibleField, toggleMasterKeyVisibilityButton));

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4a6fa5; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();
            String masterKey = isMasterKeyVisible ? masterKeyVisibleField.getText() : masterKeyField.getText();


            if (!validateInput(username, password, masterKey)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input. Please check your fields.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            boolean success = superAdminApp.login(username, password, masterKey);

            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                    success ? "Login successful!" : "Login failed. Check credentials.");
            alert.showAndWait();

            if (success) {
                navigator.navigateTo(SuperAdminOptionsScene.class);
            }
        });

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #c84b31; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(event -> navigator.goBack());

        HBox passwordBox = new HBox(5, passwordField, passwordVisibleField, togglePasswordVisibilityButton);
        passwordBox.setAlignment(Pos.CENTER);

        HBox masterKeyBox = new HBox(5, masterKeyField, masterKeyVisibleField, toggleMasterKeyVisibilityButton);
        masterKeyBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, title, usernameField, passwordBox, masterKeyBox, loginButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #fefeff; -fx-padding: 20; -fx-border-color: #8a94b8; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        return new Scene(layout, 700, 800);
    }

    private void toggleVisibility(PasswordField hiddenField, TextField visibleField, Button toggleButton) {
        if (visibleField.isVisible()) {
            hiddenField.setText(visibleField.getText());
            hiddenField.setManaged(true);
            hiddenField.setVisible(true);
            visibleField.setManaged(false);
            visibleField.setVisible(false);
            toggleButton.setText("Show");
        } else {
            visibleField.setText(hiddenField.getText());
            visibleField.setManaged(true);
            visibleField.setVisible(true);
            hiddenField.setManaged(false);
            hiddenField.setVisible(false);
            toggleButton.setText("Hide");
        }
    }

    private boolean validateInput(String username, String password, String masterKey) {
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return false;
        } else if (!MASTER_KEY_PATTERN.matcher(masterKey).matches()) {
            return false;
        }
        return true;
    }
}
