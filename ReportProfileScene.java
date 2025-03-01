package com.netconnect.Applications.UserApp.UserScenes.MenusLogin;

import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.QueryExecutor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ReportProfileScene extends ReportScene {

    private static final QueryExecutor queryExecutor = new QueryExecutor();
    private TextField reportedUsernameField;
    private TextArea reasonField;

    public ReportProfileScene(SceneNavigator navigator) {
        super(navigator);
    }

    @Override
    public Scene getScene() {
        Label titleLabel = new Label("Report a Profile");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKGREEN);

        reportedUsernameField = new TextField();
        reportedUsernameField.setPromptText("Username of reported user");
        reportedUsernameField.setMaxWidth(400);

        reasonField = new TextArea();
        reasonField.setPromptText("Reason for reporting");
        reasonField.setMaxWidth(400);
        reasonField.setWrapText(true);

        Button submitButton = new Button("Submit Report");
        submitButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        submitButton.setOnAction(event -> submitReport());

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 14px;");
        backButton.setOnAction(event -> navigator.goBack());

        VBox layout = new VBox(15, titleLabel, reportedUsernameField, reasonField, submitButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #eafaf1;");

        return new Scene(layout, 900, 1000);
    }

    @Override
    protected void submitReport() {
        if (validateInput(reasonField) && !reportedUsernameField.getText().trim().isEmpty()) {

            String queryInsert = "INSERT INTO user_reports (reported_user, reason) VALUES (?, ?);";

            try (Connection connection = queryExecutor.getAppUserConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(queryInsert);
                preparedStatement.setString(1, reportedUsernameField.getText());
                preparedStatement.setString(2, reasonField.getText());

                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                showError("Error writing to database: " + ex.getMessage());
                return;
            }

            SceneNavigator.showAlert("Profile report submitted successfully!");
            navigator.goBack();
        } else {
            showError("Please fill in all fields.");
        }
    }
}
