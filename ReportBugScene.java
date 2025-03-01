package com.netconnect.Applications.UserApp.UserScenes.MenusLogin;

import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.QueryExecutor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ReportBugScene extends ReportScene {

    private TextArea descriptionField;
    private TextArea stepsField;
    private ComboBox<String> severityField;
    private static final QueryExecutor queryExecutor = new QueryExecutor();

    public ReportBugScene(SceneNavigator navigator) {
        super(navigator);
    }

    @Override
    public Scene getScene() {
        Label titleLabel = new Label("Report a Bug");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: green;");

        descriptionField = new TextArea();
        descriptionField.setPromptText("Describe the bug");
        descriptionField.setWrapText(true);
        descriptionField.setTooltip(new Tooltip("Provide a detailed description of the bug."));

        stepsField = new TextArea();
        stepsField.setPromptText("Steps to reproduce the bug");
        stepsField.setWrapText(true);
        stepsField.setTooltip(new Tooltip("List the steps needed to reproduce the bug."));

        severityField = new ComboBox<>();
        severityField.getItems().addAll("Low", "Medium", "High");
        severityField.setPromptText("Select severity");
        severityField.setTooltip(new Tooltip("Choose the severity level of the bug."));


        Button submitButton = new Button("Submit Report");
        submitButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
        submitButton.setOnAction(event -> submitReport());


        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: lightgray; -fx-text-fill: black;");
        backButton.setOnAction(event -> navigator.goBack());


        VBox inputLayout = new VBox(10, descriptionField, stepsField, severityField);
        inputLayout.setPadding(new Insets(20));
        inputLayout.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: green; -fx-border-width: 2px;");
        inputLayout.setAlignment(Pos.CENTER);

        HBox buttonLayout = new HBox(10, backButton, submitButton);
        buttonLayout.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, titleLabel, inputLayout, buttonLayout);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f1f8e9;");

        return new Scene(layout, 900, 1000);
    }

    @Override
    protected void submitReport() {
        if (validateInput(descriptionField, stepsField) && severityField.getValue() != null) {
            String queryInsert = "INSERT INTO bug_reports (description, steps_to_reproduce, severity) VALUES (?, ?, ?);";

            try (Connection connection = queryExecutor.getAppUserConnection()) {
                PreparedStatement statement = connection.prepareStatement(queryInsert);
                statement.setString(1, descriptionField.getText());
                statement.setString(2, stepsField.getText());
                statement.setString(3, severityField.getValue());
                statement.execute();

                SceneNavigator.showAlert("Bug report submitted successfully!");
                navigator.goBack();

            } catch (SQLException e) {
                showError("Error saving bug report: " + e.getMessage());
            }
        } else {
            showError("Please complete all fields.");
        }
    }

}
