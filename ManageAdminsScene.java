package com.netconnect.Applications.SuperAdminApp.Scenes;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.SuperAdminApp.Helpers.SuperAdminNavigator;
import com.netconnect.QueryExecutor;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class ManageAdminsScene implements Sceneable {

    private final SuperAdminNavigator navigator;
    private static final QueryExecutor queryExecutor = new QueryExecutor();

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{8,}$");
    private static final Pattern MASTER_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9]{16}$");

    public ManageAdminsScene(SuperAdminNavigator superAdminNavigator) {
        this.navigator = superAdminNavigator;
    }

    @Override
    public Scene getScene() {
        Label title = new Label("Manage Admins");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Map<String, String>> adminTable = new TableView<>();
        adminTable.setPlaceholder(new Label("No admins available."));
        adminTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Map<String, String>, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get("id")));

        TableColumn<Map<String, String>, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get("username")));

        adminTable.getColumns().addAll(idColumn, usernameColumn);

        loadAdminsFromDatabase(adminTable);

        Button addAdminButton = new Button("Add Admin");
        Button deleteAdminButton = new Button("Delete Admin");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #c84b31; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(event -> navigator.goBack());

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #c84b31; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        exitButton.setOnAction(event -> navigator.close());

        addAdminButton.setOnAction(e -> showAddAdminDialog(adminTable));
        deleteAdminButton.setOnAction(e -> handleDeleteAdmin(adminTable));

        HBox buttonBox = new HBox(10, addAdminButton, deleteAdminButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, title, adminTable, buttonBox, backButton, exitButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 700, 800);
    }

    private void loadAdminsFromDatabase(TableView<Map<String, String>> adminTable) {
        adminTable.getItems().clear();
        try (Connection connection = queryExecutor.getSuperAdminConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT admin_id, admin_username FROM admins");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Map<String, String> adminData = new HashMap<>();
                adminData.put("id", String.valueOf(resultSet.getInt("admin_id")));
                adminData.put("username", resultSet.getString("admin_username"));

                adminTable.getItems().add(adminData);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load admins: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAddAdminDialog(TableView<Map<String, String>> adminTable) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Admin");

        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(10));
        dialogLayout.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextField referenceCodeField = new TextField();
        referenceCodeField.setPromptText("Reference Code");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String referenceCode = referenceCodeField.getText();

            if (!validateInput(username, password, referenceCode)) {
                showAlert("Validation Error", "Please check your input, it doesn't follow company policy", Alert.AlertType.WARNING);
                return;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String hashedReferenceCode = BCrypt.hashpw(referenceCode, BCrypt.gensalt());

            new Thread(() -> {
                boolean success = false;
                try (Connection connection = queryExecutor.getSuperAdminConnection();
                     PreparedStatement statement = connection.prepareStatement(
                             "INSERT INTO admins (admin_username, password_hash, reference_code) VALUES (?, ?, ?)")) {

                    statement.setString(1, username);
                    statement.setString(2, hashedPassword);
                    statement.setString(3, hashedReferenceCode);

                    statement.executeUpdate();
                    success = true;
                } catch (SQLException ex) {
                    Platform.runLater(() -> showAlert("Error", "Failed to add admin: " + ex.getMessage(), Alert.AlertType.ERROR));
                } finally {
                    boolean finalSuccess = success;
                    Platform.runLater(() -> {
                        dialog.setResult(ButtonType.CANCEL);
                        if (finalSuccess) {
                            loadAdminsFromDatabase(adminTable);
                        }
                    });
                }
            }).start();
        });


        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            dialog.setResult(ButtonType.CANCEL);
        });

        dialogLayout.getChildren().addAll(new Label("Enter Admin Details"), usernameField, passwordField, referenceCodeField, submitButton, exitButton);
        dialog.getDialogPane().setContent(dialogLayout);
        dialog.show();
    }



    private void handleDeleteAdmin(TableView<Map<String, String>> adminTable) {
        Map<String, String> selectedAdmin = adminTable.getSelectionModel().getSelectedItem();
        if (selectedAdmin == null) {
            showAlert("Error", "Please select an admin to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Admin");
        confirmation.setHeaderText("Are you sure you want to delete this admin?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection connection = queryExecutor.getSuperAdminConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM admins WHERE admin_id = ?")) {

                statement.setInt(1, Integer.parseInt(selectedAdmin.get("id")));
                statement.executeUpdate();
                loadAdminsFromDatabase(adminTable);
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete admin: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateInput(String username, String password, String adminKey) {
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return false;
        }
        if (!MASTER_KEY_PATTERN.matcher(adminKey).matches()) {
            return false;
        }
        return true;
    }



    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
