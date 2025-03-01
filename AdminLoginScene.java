package com.netconnect.Applications.AdminApp.AdminUI;

import com.netconnect.Applications.AdminApp.AdminApplication;
import com.netconnect.Applications.AdminApp.Helpers.AdminSceneNavigator;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
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

public class AdminLoginScene implements Sceneable {

    private final AdminSceneNavigator navigator;
    private final AdminApplication adminApp;
    private boolean isPasswordVisible = false;
    private boolean isEmployeeCodeVisible = false;



    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{8,}$");
    private static final Pattern MASTER_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9]{16}$");

    public AdminLoginScene(AdminSceneNavigator navigator, AdminApplication adminApp) {
        this.navigator = navigator;
        this.adminApp = adminApp;
    }

    @Override
    public Scene getScene() {

        Text title = new Text("Admin Login");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setFill(Color.DARKSLATEBLUE);

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

        PasswordField employeeCodeField = new PasswordField();
        employeeCodeField.setPromptText("Employee Code");
        employeeCodeField.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #8a94b8; -fx-border-radius: 5;");

        TextField employeeCodeVisibleField = new TextField();
        employeeCodeVisibleField.setPromptText("Employee Code");
        employeeCodeVisibleField.setManaged(false);
        employeeCodeVisibleField.setVisible(false);


        Button togglePasswordVisibilityButton = new Button("Show");
        togglePasswordVisibilityButton.setStyle("-fx-background-color: #dddddd;");
        togglePasswordVisibilityButton.setOnAction(e -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                passwordVisibleField.setText(passwordField.getText());
                passwordField.setManaged(false);
                passwordField.setVisible(false);
                passwordVisibleField.setManaged(true);
                passwordVisibleField.setVisible(true);
                togglePasswordVisibilityButton.setText("Hide");
            } else {
                passwordField.setText(passwordVisibleField.getText());
                passwordVisibleField.setManaged(false);
                passwordVisibleField.setVisible(false);
                passwordField.setManaged(true);
                passwordField.setVisible(true);
                togglePasswordVisibilityButton.setText("Show");
            }
        });

        Button toggleEmployeeCodeVisibilityButton = new Button("Show");
        toggleEmployeeCodeVisibilityButton.setStyle("-fx-background-color: #dddddd;");
        toggleEmployeeCodeVisibilityButton.setOnAction(e -> {
            isEmployeeCodeVisible = !isEmployeeCodeVisible;
            if (isEmployeeCodeVisible) {
                employeeCodeVisibleField.setText(employeeCodeField.getText());
                employeeCodeField.setManaged(false);
                employeeCodeField.setVisible(false);
                employeeCodeVisibleField.setManaged(true);
                employeeCodeVisibleField.setVisible(true);
                toggleEmployeeCodeVisibilityButton.setText("Hide");
            } else {
                employeeCodeField.setText(employeeCodeVisibleField.getText());
                employeeCodeVisibleField.setManaged(false);
                employeeCodeVisibleField.setVisible(false);
                employeeCodeField.setManaged(true);
                employeeCodeField.setVisible(true);
                toggleEmployeeCodeVisibilityButton.setText("Show");
            }
        });

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4a6fa5; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();
            String employeeCode = isEmployeeCodeVisible ? employeeCodeVisibleField.getText() : employeeCodeField.getText();

            if (!validateInput(username, password, employeeCode)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please check fields, it doesn't respect company policy", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            boolean success = adminApp.login(username, password, employeeCode);

            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                    success ? "Login successful!" : "Login failed. Check credentials.");
            alert.showAndWait();

            if (success) {
                navigator.navigateTo(ReportsIntermediaryScene.class);
            }
        });


        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #c84b31; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(event -> navigator.goBack());


        HBox passwordBox = new HBox(5, passwordField, passwordVisibleField, togglePasswordVisibilityButton);
        passwordBox.setAlignment(Pos.CENTER);

        HBox employeeCodeBox = new HBox(5, employeeCodeField, employeeCodeVisibleField, toggleEmployeeCodeVisibilityButton);
        employeeCodeBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, title, usernameField, passwordBox, employeeCodeBox, loginButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #fefeff; -fx-padding: 20; -fx-border-color: #8a94b8; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        return new Scene(layout, 800, 900);
    }

    private boolean validateInput(String username, String password, String adminKey) {
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return false;
        }
        if (!MASTER_KEY_PATTERN.matcher(adminKey).matches()) {
            return false;
        }
        return true;
    }
}
