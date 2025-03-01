package com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.User;
import com.netconnect.ChatRoom.Receiver;
import com.netconnect.QueryExecutor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangeGenderScene implements Sceneable {
    private final SceneNavigator navigator;
    private final User user;
    private final QueryExecutor queryExecutor = new QueryExecutor();

    public ChangeGenderScene(SceneNavigator sceneNavigator, User currentUser) {
        this.navigator = sceneNavigator;
        this.user = currentUser;
    }

    @Override
    public Scene getScene() {
        Label titleLabel = new Label("Change Gender");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton manButton = createRadioButton("Man", genderGroup);
        RadioButton womanButton = createRadioButton("Woman", genderGroup);
        RadioButton nonBinaryButton = createRadioButton("Non-Binary", genderGroup);
        RadioButton otherButton = createRadioButton("Other", genderGroup);

        Button saveButton = new Button("Save Gender");
        saveButton.setStyle("-fx-background-color: #2F855A; -fx-text-fill: white;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #276749; -fx-text-fill: white;");

        saveButton.setOnAction(event -> {
            if (genderGroup.getSelectedToggle() == null) {
                showAlert("Error", "Please select a gender.");
                return;
            }

            String selectedGender = ((RadioButton) genderGroup.getSelectedToggle()).getText();

            try {
                user.setGenderDetails(selectedGender);
                updateGenderInDatabase(selectedGender);
                showAlert("Success", "Gender updated successfully!");
                navigator.goBack();
            } catch (SQLException e) {
                showAlert("Error", "Failed to update gender. Please try again.");
                e.printStackTrace();
            }
        });

        backButton.setOnAction(event -> navigator.goBack());

        VBox layout = new VBox(15, titleLabel, manButton, womanButton, nonBinaryButton, otherButton, saveButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #F0FFF4;");

        return new Scene(layout, 900, 1000);
    }

    private RadioButton createRadioButton(String text, ToggleGroup group) {
        RadioButton radioButton = new RadioButton(text);
        radioButton.setToggleGroup(group);
        return radioButton;
    }

    private void updateGenderInDatabase(String gender) throws SQLException {
        String queryGetID = "SELECT user_id FROM user_details WHERE username = ?;";
        String query = "UPDATE users SET gender = ? WHERE id = ?";

        try (Connection connection = queryExecutor.getAppUserConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             PreparedStatement preparedStatement = connection.prepareStatement(queryGetID))
        {
            preparedStatement.setString(1, user.getUsername().getDetails());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                System.out.println(userId);
                statement.setString(1, gender);
                statement.setInt(2, userId);
                statement.executeUpdate();
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
