package com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.Details.DateOfBirthDetails;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.User;
import com.netconnect.QueryExecutor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ChangeDOBScene implements Sceneable {
    private final SceneNavigator navigator;
    private final User user;
    private final QueryExecutor queryExecutor = new QueryExecutor();

    public ChangeDOBScene(SceneNavigator sceneNavigator, User currentUser) {
        this.navigator = sceneNavigator;
        this.user = currentUser;
    }

    @Override
    public Scene getScene() {
        Label titleLabel = new Label("Change Date of Birth");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select your date of birth");

        Button saveButton = new Button("Save Date of Birth");
        saveButton.setStyle("-fx-background-color: #2F855A; -fx-text-fill: white;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #276749; -fx-text-fill: white;");

        saveButton.setOnAction(event -> {
            LocalDate selectedDate = datePicker.getValue();

            if (selectedDate == null) {
                showAlert("Error", "Please select a valid date of birth.");
                return;
            }

            try {
                user.setDateOfBirthDetails(new DateOfBirthDetails(selectedDate.toString()));
                updateDOBInDatabase(selectedDate);
                showAlert("Success", "Date of birth updated successfully!");
                navigator.goBack();
            } catch (SQLException e) {
                showAlert("Error", "Failed to update date of birth. Please try again.");
                e.printStackTrace();
            }
        });

        backButton.setOnAction(event -> navigator.goBack());

        VBox layout = new VBox(15, titleLabel, datePicker, saveButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F0FFF4;");

        return new Scene(layout, 900, 1000);
    }

    private void updateDOBInDatabase(LocalDate dob) throws SQLException {
        String queryGetID = "SELECT user_id FROM user_details WHERE username = ?;";
        String queryUpdateDOB = "UPDATE users SET date_of_birth = ? WHERE id = ?;";

        try (Connection connection = queryExecutor.getAppUserConnection();
             PreparedStatement getIdStatement = connection.prepareStatement(queryGetID);
             PreparedStatement updateDOBStatement = connection.prepareStatement(queryUpdateDOB)) {
            getIdStatement.setString(1, user.getUsername().getDetails());
            ResultSet rs = getIdStatement.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                updateDOBStatement.setString(1, dob.toString());
                updateDOBStatement.setInt(2, userId);
                updateDOBStatement.executeUpdate();
            } else {
                throw new SQLException("User ID not found for the given username.");
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
