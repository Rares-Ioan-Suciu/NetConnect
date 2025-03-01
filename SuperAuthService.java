package com.netconnect.Applications.SuperAdminApp.Helpers;

import com.netconnect.QueryExecutor;
import javafx.scene.control.Alert;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SuperAuthService {

    public boolean superAdminLogin(String username, String password, String masterKey) {
        String query = "SELECT password_hash, master_key_hash FROM super_admins WHERE admin_username = ?";
        QueryExecutor queryExecutor = new QueryExecutor();

        try (Connection connection = queryExecutor.getSuperAdminConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPasswordHash = resultSet.getString("password_hash");
                    String storedMasterKeyHash = resultSet.getString("master_key_hash");

                    boolean isPasswordValid = BCrypt.checkpw(password, storedPasswordHash);
                    boolean isMasterKeyValid = BCrypt.checkpw(masterKey, storedMasterKeyHash);

                    if (isPasswordValid && isMasterKeyValid) {
                        return true;
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Login Failed", "Invalid credentials. Please try again.");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Login Failed", "Super Admin username not found.");
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error accessing Super Admin credentials: " + e.getMessage());
            System.err.println("Database error: " + e.getMessage());
            throw new RuntimeException("Error accessing the database", e);
        }

        return false;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
