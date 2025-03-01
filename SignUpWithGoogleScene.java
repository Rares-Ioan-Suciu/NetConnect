package com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.GoogleLoginHelper;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.UserApplication;

public class SignUpWithGoogleScene implements Sceneable {

    private final SceneNavigator navigator;
    private final UserApplication userApp;

    public SignUpWithGoogleScene(SceneNavigator navigator, UserApplication userApp) {
        this.navigator = navigator;
        this.userApp = userApp;
    }

    @Override
    public Scene getScene() {

        Label googleSignUpLabel = new Label("Sign Up with Google");
        googleSignUpLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #DB4437;");

        Label instructionsLabel = new Label("Follow the steps below to sign up with Google:");
        instructionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label step1Label = new Label("Step 1: Check your browser. A Google Login page should have opened.");
        Label step2Label = new Label("Step 2: Authorize the app and copy the code displayed there.");
        Label step3Label = new Label("Step 3: Paste the code below and click Submit.");
        step1Label.setWrapText(true);
        step2Label.setWrapText(true);
        step3Label.setWrapText(true);

        TextField authUrlField = new TextField();
        authUrlField.setEditable(false);
        authUrlField.setPrefWidth(500);

        Button copyUrlButton = new Button("Copy URL");
        copyUrlButton.setStyle("-fx-background-color: #276749; -fx-text-fill: white; -fx-font-size: 14px;");
        copyUrlButton.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(authUrlField.getText());
            clipboard.setContent(content);
            showAlert(Alert.AlertType.INFORMATION, "Copied to Clipboard", "The URL has been copied to your clipboard.");
        });

        HBox urlBox = new HBox(10, authUrlField, copyUrlButton);
        urlBox.setAlignment(Pos.CENTER);

        try {
            String authorizationUrl = GoogleLoginHelper.getAuthorizationUrl();
            authUrlField.setText(authorizationUrl);
            openAuthorizationUrlInBrowser(authorizationUrl);
        } catch (Exception e) {
            authUrlField.setText("Error generating or opening authorization URL: " + e.getMessage());
        }


        TextField authorizationCodeField = new TextField();
        authorizationCodeField.setPromptText("Enter the authorization code here");
        authorizationCodeField.setPrefWidth(500);

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #2F855A; -fx-text-fill: white; -fx-font-size: 14px;");
        submitButton.setOnAction(event -> {
            String authorizationCode = authorizationCodeField.getText().trim();

            if (authorizationCode.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Authorization Code Missing", "Please enter the authorization code.");
                return;
            }

            try {
                GoogleLoginHelper.GoogleUser googleUser = GoogleLoginHelper.authenticateWithCode(authorizationCode);

                if (googleUser != null) {
                    System.out.println("Google User: " + googleUser);
                    try {
                        userApp.googleSignUp(googleUser);
                        navigator.setUser(userApp.getCurrentUser());
                        showAlert(Alert.AlertType.INFORMATION, "Sign Up Successful", "Welcome, " + googleUser.getName() +".\n Please make sure that you update gender,birthday and password if you wish to do so. Default password is GoogleAuth1?!");
                        navigator.navigateTo(UserGeneralDetailsScene.class);
                    } catch (Exception signUpException) {
                        showAlert(Alert.AlertType.ERROR, "Sign Up Error", "An error occurred during sign up: " + signUpException.getMessage());
                        signUpException.printStackTrace();
                    }
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Sign Up Error", e.getMessage());
                e.printStackTrace();
            }
        });

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #276749; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(event -> navigator.goBack());

        VBox layout = new VBox(15,
                googleSignUpLabel,
                instructionsLabel,
                step1Label,
                step2Label,
                step3Label,
                urlBox,
                authorizationCodeField,
                submitButton,
                backButton
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F9F9F9;");

        return new Scene(layout, 900, 1000);
    }

    private void openAuthorizationUrlInBrowser(String authorizationUrl) {
        PauseTransition delay = new PauseTransition(javafx.util.Duration.seconds(3));
        delay.setOnFinished(event -> {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                String[] command = os.contains("win") ? new String[]{"rundll32", "url.dll,FileProtocolHandler", authorizationUrl}
                        : os.contains("mac") ? new String[]{"open", authorizationUrl}
                        : os.contains("nix") || os.contains("nux") ? new String[]{"xdg-open", authorizationUrl}
                        : new String[]{};

                if (command.length > 0) {
                    Runtime.getRuntime().exec(command);
                } else {
                    System.out.println("Unsupported OS. Please open the URL manually: " + authorizationUrl);
                }
            } catch (Exception e) {
                System.err.println("Error opening browser: " + e.getMessage());
            }
        });
        delay.play();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
