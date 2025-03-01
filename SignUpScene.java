package com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess;

import com.netconnect.Applications.UserApp.Details.Email;
import com.netconnect.Applications.UserApp.Details.Password;
import com.netconnect.Applications.UserApp.Details.Username;
import com.netconnect.Applications.UserApp.Exception.InvalidEmailException;
import com.netconnect.Applications.UserApp.Exception.PasswordException;
import com.netconnect.Applications.UserApp.Exception.UsernameAlreadyExistsException;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.UserScenes.MenusLogin.ReportBugScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.UserApplication;

public class SignUpScene implements Sceneable {

    private final SceneNavigator navigator;
    private final UserApplication userApp;

    public SignUpScene(SceneNavigator navigator, UserApplication userApp) {
        this.navigator = navigator;
        this.userApp = userApp;
    }

    public Scene getScene() {

        Label signUpLabel = new Label("Sign Up");
        signUpLabel.setFont(new Font("Arial", 24));
        signUpLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2F855A;");
        signUpLabel.setTextAlignment(TextAlignment.CENTER);


        Button googleSignUpButton = new Button("Sign Up with Google");
        googleSignUpButton.setStyle("-fx-background-color: #DB4437; -fx-text-fill: white; -fx-font-size: 14px;");
        googleSignUpButton.setOnAction(event -> navigator.navigateTo(SignUpWithGoogleScene.class));


        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField passwordConfirmField = new PasswordField();
        passwordConfirmField.setPromptText("Confirm Password");

        TextField passwordVisibleField = new TextField();
        passwordVisibleField.setPromptText("Password");
        passwordVisibleField.setManaged(false);
        passwordVisibleField.setVisible(false);

        TextField passwordConfirmVisibleField = new TextField();
        passwordConfirmVisibleField.setPromptText("Confirm Password");
        passwordConfirmVisibleField.setManaged(false);
        passwordConfirmVisibleField.setVisible(false);


        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        passwordConfirmVisibleField.textProperty().bindBidirectional(passwordConfirmField.textProperty());

        CheckBox showPasswordCheckBox = new CheckBox("Show Password");
        showPasswordCheckBox.setOnAction(event -> {
            boolean isSelected = showPasswordCheckBox.isSelected();

            passwordField.setVisible(!isSelected);
            passwordField.setManaged(!isSelected);
            passwordVisibleField.setVisible(isSelected);
            passwordVisibleField.setManaged(isSelected);

            passwordConfirmField.setVisible(!isSelected);
            passwordConfirmField.setManaged(!isSelected);
            passwordConfirmVisibleField.setVisible(isSelected);
            passwordConfirmVisibleField.setManaged(isSelected);
        });


        Button signUpButton = new Button("Register");
        signUpButton.setStyle("-fx-background-color: #2F855A; -fx-text-fill: white; -fx-font-size: 14px;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #276749; -fx-text-fill: white; -fx-font-size: 14px;");

        Button reportBug = new Button("Report Bug");
        reportBug.setStyle("-fx-background-color: #68D391; -fx-text-fill: white; -fx-font-size: 14px;");

        signUpButton.setOnAction(event -> {
            if (emailField.getText().isEmpty() || usernameField.getText().isEmpty() ||
                    passwordField.getText().isEmpty() || passwordConfirmField.getText().isEmpty()) {
                SceneNavigator.showAlert("Please fill in all fields.");
                return;
            }
            Username username = new Username(usernameField.getText());
            Email email = new Email(emailField.getText());
            Password password = new Password(passwordField.getText());


            try {
                username.verifyInfo();
                email.verifyInfo();

                if (!passwordField.getText().equals(passwordConfirmField.getText())) {
                    SceneNavigator.showAlert("Passwords do not match.");
                    return;
                }

                password.verifyInfo();
                navigator.setUser(userApp.signUp(username, email, password));
                navigator.navigateTo(UserPrivateDetailsScene.class);

            } catch (UsernameAlreadyExistsException | InvalidEmailException | PasswordException e) {
                SceneNavigator.showAlert(e.getMessage());
            }
        });

        backButton.setOnAction(event -> navigator.goBack());
        reportBug.setOnAction(event -> navigator.navigateTo(ReportBugScene.class));


        VBox layout = new VBox(15, signUpLabel, usernameField, emailField,
                passwordField, passwordVisibleField,
                passwordConfirmField, passwordConfirmVisibleField,
                showPasswordCheckBox, googleSignUpButton, signUpButton, backButton, reportBug);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F0FFF4;");

        return new Scene(layout, 900, 1000);
    }
}
