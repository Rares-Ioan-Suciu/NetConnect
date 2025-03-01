package com.netconnect.Applications.AdminApp.AdminUI;

import com.netconnect.Applications.AdminApp.Helpers.AdminSceneNavigator;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.QueryExecutor;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportsBugsScene implements Sceneable {

    private final AdminSceneNavigator navigator;
    private final static QueryExecutor queryExecutor = new QueryExecutor();

    public ReportsBugsScene(AdminSceneNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public Scene getScene() {

        Label title = new Label("Bug Reports");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2f4f2f;");

        List<String> bugReportIds = fetchReportIds();
        ListView<String> reportFileView = new ListView<>();
        reportFileView.getItems().addAll(bugReportIds);
        reportFileView.setStyle("-fx-border-color: #66bb6a; -fx-border-width: 1;");

        TextArea reportDetailsArea = new TextArea();
        reportDetailsArea.setEditable(false);
        reportDetailsArea.setPromptText("Select a report to view details...");
        reportDetailsArea.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        TextArea responseArea = new TextArea();
        responseArea.setPromptText("Enter your response...");
        responseArea.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        Button respondButton = getRespondButton(reportFileView, responseArea);
        respondButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> navigator.goBack());
        backButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        reportFileView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showReportDetails(newValue, reportDetailsArea);
            }
        });

        VBox layout = new VBox(15, title, reportFileView, reportDetailsArea, responseArea, respondButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 30; -fx-background-color: #e8f5e9; -fx-border-color: #66bb6a; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        return new Scene(layout, 800, 900);
    }

    private List<String> fetchReportIds() {
        List<String> reportIds = new ArrayList<>();
        String query = "SELECT report_id FROM bug_reports";

        try (Connection conn = queryExecutor.getAppAdminConnection();
             PreparedStatement getReports = conn.prepareStatement(query);
             ResultSet rs = getReports.executeQuery()) {

            while (rs.next()) {
                reportIds.add(rs.getString("report_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportIds;
    }


    private void showReportDetails(String reportId, TextArea reportDetailsArea) {
        String query = "SELECT * FROM bug_reports WHERE report_id = ?";
        try (Connection conn = queryExecutor.getAppAdminConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, reportId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String description = rs.getString("description");
                    String stepsToReproduce = rs.getString("steps_to_reproduce");
                    String severity = rs.getString("severity");
                    String date = rs.getString("report_date");
                    String adminResponse = rs.getString("admin_response");

                    String reportDetails = String.format(
                            "Description: %s\nSteps to Reproduce: %s\nSeverity: %s\nDate: %s\nAdmin Response: %s",
                            description, stepsToReproduce, severity, date, adminResponse);
                    reportDetailsArea.setText(reportDetails);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Button getRespondButton(ListView<String> reportFileView, TextArea responseArea) {
        Button respondButton = new Button("Respond to Selected Report");
        respondButton.setOnAction(event -> {
            String selectedReportId = reportFileView.getSelectionModel().getSelectedItem();
            if (selectedReportId != null) {
                if(!IsValid(responseArea.getText())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input. Please check your field.", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }
                updateResponse(selectedReportId, responseArea.getText());
                responseArea.clear();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Response added.");
                alert.showAndWait();
            }
        });
        return respondButton;
    }

    private void updateResponse(String reportId, String responseText) {
        String updateQuery = "UPDATE bug_reports SET admin_response = ? WHERE report_id = ?";

        try (Connection conn = queryExecutor.getAppAdminConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, responseText);
            stmt.setString(2, reportId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Response updated successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean IsValid(String responseText) {
        if (responseText.trim().isEmpty()) {
            return false;
        }

        int maxLength = 500;
        if (responseText.length() > maxLength) {
            return false;
        }
        String regex = "^[a-zA-Z0-9\\s.,!?]*$";
        if (!responseText.matches(regex)) {
            return false;
        }

        return true;
    }
}
