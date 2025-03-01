package com.netconnect.Applications.UserApp.UserScenes.MenusLogin;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.GoogleLoginHelper;
import com.netconnect.QueryExecutor;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginWithGoogleScene implements Sceneable {

    private final SceneNavigator navigator;
    private final UserApplication userApp;

    public LoginWithGoogleScene(SceneNavigator navigator, UserApplication userApp) {
        this.navigator = navigator;
        this.userApp = userApp;
    }

    @Override
    public Scene getScene() {

        Label googleLoginLabel = new Label("Login with Google");
        googleLoginLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #DB4437;");

        Label instructionsLabel = new Label("1. A Google Login page has opened in your browser.\n" +
                "2. Authorize the app and copy the code displayed there.\n" +
                "3. Paste the code into the field below and click Submit.");
        instructionsLabel.setWrapText(true);
        instructionsLabel.setPadding(new Insets(10));

        TextField authUrlField = new TextField("Your authorization URL will appear here.");
        authUrlField.setEditable(false);
        authUrlField.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ccc;");

        Button copyButton = new Button("Copy URL");
        copyButton.setStyle("-fx-background-color: #276749; -fx-text-fill: white; -fx-font-size: 14px;");
        copyButton.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(authUrlField.getText());
            clipboard.setContent(content);
            showAlert(Alert.AlertType.INFORMATION, "Copied", "Authorization URL copied to clipboard.");
        });

        HBox urlBox = new HBox(10, authUrlField, copyButton);
        urlBox.setAlignment(Pos.CENTER);
        urlBox.setPadding(new Insets(10));


        try {
            String authorizationUrl = GoogleLoginHelper.getMinimalAuthorizationUrl();
            authUrlField.setText(authorizationUrl);
            openAuthorizationUrlInBrowser(authorizationUrl);
        } catch (Exception e) {
            authUrlField.setText("Error generating authorization URL.");
            e.printStackTrace();
        }

        TextField authorizationCodeField = new TextField();
        authorizationCodeField.setPromptText("Enter the authorization code here");


        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #2F855A; -fx-text-fill: white; -fx-font-size: 14px;");
        submitButton.setOnAction(event -> handleAuthorizationCode(authorizationCodeField.getText().trim()));


        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #276749; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(event -> navigator.goBack());

        VBox layout = new VBox(15, googleLoginLabel, instructionsLabel, urlBox, authorizationCodeField, submitButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F9F9F9;");

        return new Scene(layout, 900, 1000);
    }

    private void handleAuthorizationCode(String authorizationCode) {
        if (authorizationCode.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Authorization code is missing.");
            return;
        }

        try {
            GoogleLoginHelper.GoogleUser googleUser = GoogleLoginHelper.authenticateWithMinimalCode(authorizationCode);
            if (userApp.googleLogin(googleUser)) {
                navigator.setUser(userApp.getCurrentUser());
                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, " + googleUser.getName() + "!");
                navigator.navigateTo(MainScreenScene.class);
            } else if (isBanned(googleUser.getEmail())) {
                promptSignUp(googleUser);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "The user of this account has been banned.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Failed to login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isBanned(String email) {
        String getBanned = "SELECT banned FROM user_details where email = ?;";
        System.out.println(email);
        QueryExecutor queryExecutor = new QueryExecutor();
        try(Connection connection = queryExecutor.getAppUserConnection();
        PreparedStatement statement = connection.prepareStatement(getBanned)) {
            statement.setString(1, email);
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            resultSet.next();
            return resultSet.getInt("banned") != 1;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void promptSignUp(GoogleLoginHelper.GoogleUser googleUser) {
        Alert signUpPrompt = new Alert(Alert.AlertType.CONFIRMATION);
        signUpPrompt.setTitle("Sign Up");
        signUpPrompt.setHeaderText("No account found.");
        signUpPrompt.setContentText("Would you like to sign up with your Google account?");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        signUpPrompt.getButtonTypes().setAll(yesButton, noButton);

        signUpPrompt.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                try {
                    userApp.googleSignUp(googleUser);
                    navigator.setUser(userApp.getCurrentUser());
                    showAlert(Alert.AlertType.INFORMATION, "Sign Up Successful", "Welcome, " + googleUser.getName() +".\n Please make sure that you update gender,birthday and password if you wish to do so. Default password is GoogleAuth1?!");
                    navigator.navigateTo(MainScreenScene.class);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Sign Up Error", "An error occurred: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
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
