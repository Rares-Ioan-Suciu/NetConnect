package com.netconnect.Applications.UserApp.UserScenes.MenusLogin;

import com.netconnect.Applications.UserApp.Details.Username;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.Helpers.Auth;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.UserApplication;

public class LoginScene implements Sceneable {

    private final SceneNavigator navigator;
    private final UserApplication userApp;
    private boolean isPasswordVisible = false;

    public LoginScene(SceneNavigator navigator, UserApplication userApp) {
        this.navigator = navigator;
        this.userApp = userApp;
    }

    @Override
    public Scene getScene() {

        Label loginLabel = new Label("Login");
        loginLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2F855A;");


        Button googleLoginButton = new Button("Login with Google");
        googleLoginButton.setStyle("-fx-background-color: #DB4437; -fx-text-fill: white; -fx-font-size: 14px;");
        googleLoginButton.setOnAction(event -> navigator.navigateTo(LoginWithGoogleScene.class));

        TextField usernameField = new TextField();
        usernameField.setPrefWidth(50);
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextField passwordVisibleField = new TextField();
        passwordVisibleField.setPromptText("Password");
        passwordVisibleField.setManaged(false);
        passwordVisibleField.setVisible(false);

        Button togglePasswordVisibilityButton = new Button("Show");
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

        Button loginButton = new Button("Submit");
        loginButton.setStyle("-fx-background-color: #2F855A; -fx-text-fill: white; -fx-font-size: 14px;");
        loginButton.setOnAction(event -> {
            String usernameText = usernameField.getText();
            String passwordText = isPasswordVisible ? passwordVisibleField.getText() : passwordField.getText();

            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Please fill in all fields.");
                return;
            }

            Username username = new Username(usernameText);

            if (userApp.login(username, passwordText) != null) {
                navigator.setUser(userApp.login(username, passwordText));
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "You have successfully logged in.");
                navigator.navigateTo(MainScreenScene.class);
            } else if (Auth.getDiagnosis() == 1)
            {
                showAlert(Alert.AlertType.ERROR, "This user is banned", "BANNED!\nPlease contact customer support for more information.");
            }
            else if(Auth.getDiagnosis() == 2) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Please check your credentials and try again, user not found.");
            }
            else
            {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Something went terribly wrong.");
            }
        });


        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #276749; -fx-t7ext-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(event -> navigator.goBack());


        Button reportBugButton = new Button("Report Bug");
        reportBugButton.setStyle("-fx-background-color: #68D391; -fx-text-fill: white; -fx-font-size: 14px;");
        reportBugButton.setOnAction(event -> navigator.navigateTo(ReportBugScene.class));


        HBox passwordBox = new HBox(5, passwordField, passwordVisibleField, togglePasswordVisibilityButton);
        passwordBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(10, googleLoginButton, loginButton, backButton, reportBugButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, loginLabel, usernameField, passwordBox, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F0FFF4;");

        return new Scene(layout, 900, 1000);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
