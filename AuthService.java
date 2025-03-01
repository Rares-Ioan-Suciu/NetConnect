package com.netconnect.Applications.AdminApp.Helpers;

import com.netconnect.QueryExecutor;
import javafx.scene.control.Alert;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    public boolean adminLogin(String username, String password, String employeeCode) {
        String query = "SELECT password_hash, reference_code FROM admins WHERE admin_username = ?";
        QueryExecutor queryExecutor = new QueryExecutor();

        try (Connection connection = queryExecutor.getAppAdminConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPasswordHash = resultSet.getString("password_hash");
                    String storedReferenceCode = resultSet.getString("reference_code");

                    boolean isPasswordValid = BCrypt.checkpw(password, storedPasswordHash);
                    boolean isCodeValid = BCrypt.checkpw(employeeCode, storedReferenceCode);

                    if (isPasswordValid && isCodeValid) {
                        return true;
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Login Failed", "Invalid credentials. Please try again.");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Login Failed", "Admin username not found.");
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error accessing admin credentials: " + e.getMessage());
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
