package com.netconnect.Applications.SuperAdminApp.Scenes;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.SuperAdminApp.Helpers.SuperAdminNavigator;
import com.netconnect.QueryExecutor;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.netconnect.Applications.SuperAdminApp.Scenes.ReportSuperProfilesScene.getStrings;

public class ReportsSuperBugsScene implements Sceneable {

    private final SuperAdminNavigator navigator;
    private static final QueryExecutor queryExecutor = new QueryExecutor();

    public ReportsSuperBugsScene(SuperAdminNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public Scene getScene() {
        Label title = new Label("Superadmin - Bug Reports");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2f4f2f;");

        List<String> bugReportIds = fetchReportIds();
        ListView<String> reportFileView = new ListView<>();
        reportFileView.getItems().addAll(bugReportIds);
        reportFileView.setStyle("-fx-border-color: #66bb6a; -fx-border-width: 1;");

        TextArea reportDetailsArea = new TextArea();
        reportDetailsArea.setEditable(false);
        reportDetailsArea.setPromptText("Select a report to view details...");
        reportDetailsArea.setStyle("-fx-font-size: 14px; -fx-padding: 5;");

        ComboBox<String> adminSelector = new ComboBox<>();
        adminSelector.setPromptText("Assign to Admin");
        adminSelector.getItems().addAll(fetchAdminUsernames());

        Button assignButton = new Button("Assign");
        assignButton.setOnAction(event -> {
            String selectedReportId = reportFileView.getSelectionModel().getSelectedItem();
            String selectedAdmin = adminSelector.getValue();
            if (selectedReportId != null && selectedAdmin != null) {
                assignReportToAdmin(selectedReportId, selectedAdmin);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Report assigned to " + selectedAdmin);
                alert.showAndWait();
            }
        });

        ComboBox<String> severitySelector = new ComboBox<>();
        severitySelector.getItems().addAll("Low", "Medium", "High", "Critical");
        severitySelector.setPromptText("Set Priority");

        Button prioritizeButton = new Button("Set Priority");
        prioritizeButton.setOnAction(event -> {
            String selectedReportId = reportFileView.getSelectionModel().getSelectedItem();
            String selectedSeverity = severitySelector.getValue();
            if (selectedReportId != null && selectedSeverity != null) {
                prioritizeReport(selectedReportId, selectedSeverity);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Priority set to " + selectedSeverity);
                alert.showAndWait();
            }
        });

        Button deleteButton = new Button("Delete Selected Report");
        deleteButton.setOnAction(event -> {
            String selectedReportId = reportFileView.getSelectionModel().getSelectedItem();
            if (selectedReportId != null) {
                deleteReport(selectedReportId);
                reportFileView.getItems().remove(selectedReportId);
                reportDetailsArea.clear();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Report deleted.");
                alert.showAndWait();
            }
        });
        deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        reportFileView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showReportDetails(newValue, reportDetailsArea);
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> navigator.goBack());
        backButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        HBox adminActions = new HBox(10, adminSelector, assignButton);
        adminActions.setAlignment(Pos.CENTER);

        HBox priorityActions = new HBox(10, severitySelector, prioritizeButton);
        priorityActions.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, title, reportFileView, reportDetailsArea, adminActions, priorityActions, deleteButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 30; -fx-background-color: #e8f5e9; -fx-border-color: #66bb6a; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        return new Scene(layout, 700, 800);
    }

    private List<String> fetchReportIds() {
        List<String> reportIds = new ArrayList<>();
        String query = "SELECT report_id FROM bug_reports";

        try (Connection conn = queryExecutor.getSuperAdminConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reportIds.add(rs.getString("report_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportIds;
    }

    private List<String> fetchAdminUsernames() {
        return getStrings(queryExecutor);
    }

    private void assignReportToAdmin(String reportId, String adminUsername) {
        String updateQuery = "UPDATE bug_reports SET assigned_to = ? WHERE report_id = ?";
        try (Connection conn = queryExecutor.getSuperAdminConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, adminUsername);
            stmt.setString(2, reportId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void prioritizeReport(String reportId, String severity) {
        String updateQuery = "UPDATE bug_reports SET severity = ? WHERE report_id = ?";
        try (Connection conn = queryExecutor.getSuperAdminConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, severity);
            stmt.setString(2, reportId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showReportDetails(String reportId, TextArea reportDetailsArea) {
        String query = "SELECT * FROM bug_reports WHERE report_id = ?";
        try (Connection conn = queryExecutor.getSuperAdminConnection();
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

    private void deleteReport(String reportId) {
        String deleteQuery = "DELETE FROM bug_reports WHERE report_id = ?";
        try (Connection conn = queryExecutor.getSuperAdminConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setString(1, reportId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
