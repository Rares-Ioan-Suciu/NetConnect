package com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.User;
import com.netconnect.QueryExecutor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChangePasswordScene implements Sceneable {
    private final SceneNavigator navigator;
    private final User user;
    private final QueryExecutor queryExecutor = new QueryExecutor();

    public ChangePasswordScene(SceneNavigator sceneNavigator, User currentUser) {
        this.navigator = sceneNavigator;
        this.user = currentUser;
    }

    @Override
    public Scene getScene() {
        Label titleLabel = new Label("Change Password");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        TextField currentPasswordVisible = new TextField();
        currentPasswordVisible.setPromptText("Current Password");
        currentPasswordVisible.setManaged(false);
        currentPasswordVisible.setVisible(false);

        TextField newPasswordVisible = new TextField();
        newPasswordVisible.setPromptText("New Password");
        newPasswordVisible.setManaged(false);
        newPasswordVisible.setVisible(false);

        TextField confirmPasswordVisible = new TextField();
        confirmPasswordVisible.setPromptText("Confirm New Password");
        confirmPasswordVisible.setManaged(false);
        confirmPasswordVisible.setVisible(false);

        CheckBox togglePasswordVisibility = new CheckBox("Show Passwords");
        togglePasswordVisibility.setOnAction(event -> {
            boolean isSelected = togglePasswordVisibility.isSelected();

            toggleFieldVisibility(currentPasswordField, currentPasswordVisible, isSelected);
            toggleFieldVisibility(newPasswordField, newPasswordVisible, isSelected);
            toggleFieldVisibility(confirmPasswordField, confirmPasswordVisible, isSelected);
        });

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setStyle("-fx-background-color: #2F855A; -fx-text-fill: white;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #276749; -fx-text-fill: white;");

        changePasswordButton.setOnAction(event -> {
            String currentPassword = togglePasswordVisibility.isSelected() ? currentPasswordVisible.getText() : currentPasswordField.getText();
            String newPassword = togglePasswordVisibility.isSelected() ? newPasswordVisible.getText() : newPasswordField.getText();
            String confirmPassword = togglePasswordVisibility.isSelected() ? confirmPasswordVisible.getText() : confirmPasswordField.getText();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Error", "All fields are required.");
                return;
            }

            try {
                String currentPasswordHash = getPasswordHashFromDatabase(user.getUsername().getDetails());

                if (!BCrypt.checkpw(currentPassword, currentPasswordHash)) {
                    showAlert("Error", "Current password is incorrect.");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showAlert("Error", "New passwords do not match.");
                    return;
                }

                if (currentPassword.equals(newPassword)) {
                    showAlert("Error", "New password cannot be the same as the current password.");
                    return;
                }

                updatePasswordInDatabase(newPassword);
                showAlert("Success", "Password updated successfully!");
                navigator.goBack();
            } catch (SQLException e) {
                showAlert("Error", "Failed to update password. Please try again.");
                e.printStackTrace();
            }
        });

        backButton.setOnAction(event -> navigator.goBack());

        VBox layout = new VBox(15, titleLabel,
                wrapWithToggle(currentPasswordField, currentPasswordVisible),
                wrapWithToggle(newPasswordField, newPasswordVisible),
                wrapWithToggle(confirmPasswordField, confirmPasswordVisible),
                togglePasswordVisibility, changePasswordButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F0FFF4;");

        return new Scene(layout, 900, 1000);
    }

    private HBox wrapWithToggle(PasswordField passwordField, TextField textField) {
        HBox hBox = new HBox(10, passwordField, textField);
        hBox.setAlignment(Pos.CENTER);
        return hBox;
    }

    private void toggleFieldVisibility(PasswordField passwordField, TextField textField, boolean isVisible) {
        if (isVisible) {
            textField.setText(passwordField.getText());
            textField.setVisible(true);
            textField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(textField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            textField.setVisible(false);
            textField.setManaged(false);
        }
    }

    private void updatePasswordInDatabase(String newPassword) throws SQLException {
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String updatePasswordQuery = "UPDATE user_details SET password_hash = ? WHERE username = ?";

        try (Connection connection = queryExecutor.getAppUserConnection();
             PreparedStatement statement = connection.prepareStatement(updatePasswordQuery)) {
            statement.setString(1, hashedPassword);
            statement.setString(2, user.getUsername().getDetails());
            statement.executeUpdate();
        }
    }

    private String getPasswordHashFromDatabase(String username) throws SQLException {
        String query = "SELECT password_hash FROM user_details WHERE username = ?";
        try (Connection connection = queryExecutor.getAppUserConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("password_hash");
            } else {
                throw new SQLException("User not found.");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
