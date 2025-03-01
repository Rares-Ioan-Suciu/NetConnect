package com.netconnect.Applications.SuperAdminApp.Scenes;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.SuperAdminApp.Helpers.SuperAdminNavigator;
import com.netconnect.QueryExecutor;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportSuperProfilesScene implements Sceneable {

    private final SuperAdminNavigator navigator;
    private ComboBox<String> adminComboBox;
    private static final QueryExecutor queryExecutor = new QueryExecutor();

    public ReportSuperProfilesScene(SuperAdminNavigator superAdminNavigator) {
        this.navigator = superAdminNavigator;
    }

    @Override
    public Scene getScene() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("SuperAdmin - User Reports");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        layout.getChildren().add(titleLabel);

        ListView<String> reportListView = new ListView<>();
        reportListView.getItems().addAll(fetchReports());
        reportListView.setStyle("-fx-border-color: #66bb6a; -fx-border-width: 1;");
        layout.getChildren().add(reportListView);

        TextArea reportDetailsArea = new TextArea();
        reportDetailsArea.setEditable(false);
        reportDetailsArea.setPromptText("Select a report to view details...");
        layout.getChildren().add(reportDetailsArea);

        reportListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showReportDetails(newValue, reportDetailsArea);
            }
        });

        initializeAdminComboBox();
        HBox adminActions = new HBox(10, adminComboBox, getAssignAdminButton(reportListView));
        adminActions.setAlignment(Pos.CENTER);
        layout.getChildren().add(adminActions);

        Button deleteButton = getDeleteReportButton(reportListView);
        deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");
        layout.getChildren().add(deleteButton);

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> navigator.goBack());
        backButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-weight: bold;");
        layout.getChildren().add(backButton);

        return new Scene(layout, 700, 800);
    }

    private void initializeAdminComboBox() {
        adminComboBox = new ComboBox<>();
        adminComboBox.getItems().addAll(fetchAdminNames());
        adminComboBox.setPromptText("Select an Admin");
    }

    private List<String> fetchReports() {
        List<String> reports = new ArrayList<>();
        String query = "SELECT report_id FROM user_reports";

        try (Connection conn = queryExecutor.getSuperAdminConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reports.add(rs.getString("report_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    private List<String> fetchAdminNames() {
        return getStrings(queryExecutor);
    }

    @NotNull
    static List<String> getStrings(QueryExecutor queryExecutor) {
        List<String> admins = new ArrayList<>();
        String query = "SELECT admin_username FROM admins";

        try (Connection conn = queryExecutor.getSuperAdminConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                admins.add(rs.getString("admin_username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    private void showReportDetails(String reportId, TextArea reportDetailsArea) {
        String query = "SELECT * FROM user_reports WHERE report_id = ?";
        try (Connection conn = queryExecutor.getSuperAdminConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, reportId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String reportedUser = rs.getString("reported_user");
                    String reason = rs.getString("reason");
                    String date = rs.getString("report_date");
                    String assignedTo = rs.getString("assigned_to");

                    String reportDetails = String.format(
                            "ReportedUser: %s\nReason: %s\nDate: %s\nAssigned To: %s",
                            reportedUser, reason,date, assignedTo == null ? "Unassigned" : assignedTo);
                    reportDetailsArea.setText(reportDetails);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Button getAssignAdminButton(ListView<String> reportListView) {
        Button assignButton = new Button("Assign to Admin");
        assignButton.setOnAction(event -> {
            String selectedReportId = reportListView.getSelectionModel().getSelectedItem();
            String selectedAdmin = adminComboBox.getValue();
            if (selectedReportId != null && selectedAdmin != null) {
                assignReportToAdmin(selectedReportId, selectedAdmin);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Report assigned to " + selectedAdmin);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select both a report and an admin.");
                alert.showAndWait();
            }
        });
        return assignButton;
    }

    private void assignReportToAdmin(String reportId, String adminName) {
        String updateQuery = "UPDATE user_reports SET assigned_to = ? WHERE report_id = ?";

        try (Connection conn = queryExecutor.getSuperAdminConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, adminName);
            stmt.setString(2, reportId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Button getDeleteReportButton(ListView<String> reportListView) {
        Button deleteButton = new Button("Delete Report");
        deleteButton.setOnAction(event -> {
            String selectedReportId = reportListView.getSelectionModel().getSelectedItem();
            if (selectedReportId != null) {
                deleteReport(selectedReportId);
                reportListView.getItems().remove(selectedReportId);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Report deleted.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "No report selected.");
                alert.showAndWait();
            }
        });
        return deleteButton;
    }

    private void deleteReport(String reportId) {
        String deleteQuery = "DELETE FROM user_reports WHERE report_id = ?";
        try (Connection conn = queryExecutor.getSuperAdminConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setString(1, reportId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
