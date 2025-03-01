package com.netconnect.Applications.SuperAdminApp.Scenes;

import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.Applications.SuperAdminApp.Helpers.SuperAdminNavigator;
import com.netconnect.Applications.SuperAdminApp.SuperAdminApp;
import com.netconnect.QueryExecutor;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ManageUsersScene implements Sceneable {

    private final SuperAdminNavigator navigator;
    private static final QueryExecutor queryExecutor = new QueryExecutor();

    public ManageUsersScene(SuperAdminNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public Scene getScene() {
        Label title = new Label("Manage Users");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<Object[]> userTable = new TableView<>();
        userTable.setPlaceholder(new Label("No users available."));
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Object[], Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>((Integer) data.getValue()[0]));

        TableColumn<Object[], String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>((String) data.getValue()[1]));

        TableColumn<Object[], String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>((String) data.getValue()[2]));

        userTable.getColumns().addAll(idColumn, usernameColumn, emailColumn);

        loadUsersFromDatabase(userTable);

        Button banUserButton = new Button("Ban User");
        Button deletePhotoButton = new Button("Delete Profile Photo");
        Button deleteUserButton = new Button("Delete User");

        banUserButton.setOnAction(e -> handleBanUser(userTable));
        deletePhotoButton.setOnAction(e -> handleDeletePhoto(userTable));
        deleteUserButton.setOnAction(e -> handleDeleteUser(userTable));

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #c84b31; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(event -> navigator.goBack());

        Button exitButton = new Button("Exit");
        exitButton.setStyle("-fx-background-color: #c84b31; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        exitButton.setOnAction(event -> navigator.close());

        HBox buttonBox = new HBox(10, banUserButton, deletePhotoButton, deleteUserButton, backButton, exitButton); // Added deleteUserButton
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, title, userTable, buttonBox);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 700, 800);
    }

    private void loadUsersFromDatabase(TableView<Object[]> userTable) {
        userTable.getItems().clear();
        try (Connection connection = queryExecutor.getSuperAdminConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id, username, email FROM user_details");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");

                userTable.getItems().add(new Object[]{id, username, email});
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load users: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleBanUser(TableView<Object[]> userTable) {
        Object[] selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to ban.", Alert.AlertType.WARNING);
            return;
        }

        int userId = (Integer) selectedUser[0];
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Ban User");
        confirmation.setHeaderText("Are you sure you want to ban this user?");
        confirmation.setContentText("This action will prevent the user from accessing the application.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection connection = queryExecutor.getSuperAdminConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE user_details SET banned = true WHERE id = ?")) {

                statement.setInt(1, userId);
                statement.executeUpdate();
                showAlert("Success", "User banned successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to ban user: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void handleDeletePhoto(TableView<Object[]> userTable) {
        Object[] selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to delete their photo.", Alert.AlertType.WARNING);
            return;
        }

        int userId = (Integer) selectedUser[0];
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Profile Photo");
        confirmation.setHeaderText("Are you sure you want to delete this user's profile photo?");
        confirmation.setContentText("This action will permanently delete the photo.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection connection = queryExecutor.getSuperAdminConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE users SET profile_picture_url = NULL WHERE id = (SELECT user_id FROM user_details WHERE user_id = ?);")) {

                statement.setInt(1, userId);
                statement.executeUpdate();
                showAlert("Success", "Profile photo deleted successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete profile photo: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }


    private void handleDeleteUser(TableView<Object[]> userTable) {
        Object[] selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to delete.", Alert.AlertType.WARNING);
            return;
        }

        int ID = (Integer) selectedUser[0];

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete User");
        confirmation.setHeaderText("Are you sure you want to delete this user?");
        confirmation.setContentText("This action will permanently delete the user from the system.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection connection = queryExecutor.getSuperAdminConnection()) {
                int user_id = 0;
                try(PreparedStatement statementGetUserID = connection.prepareStatement("SELECT user_id FROM user_details WHERE id = ?;"))
                {
                    statementGetUserID.setInt(1, ID);
                    try (ResultSet resultSetGetUserID = statementGetUserID.executeQuery()) {
                        while (resultSetGetUserID.next()) {
                            user_id = resultSetGetUserID.getInt("user_id");
                        }
                    }
                    catch (SQLException e) {
                        showAlert("Error", "Something went terribly wrong", Alert.AlertType.ERROR);
                    }
                }

                connection.setAutoCommit(false);

                try (PreparedStatement statement_user_details = connection.prepareStatement("DELETE FROM user_details WHERE id = ?");
                     PreparedStatement statement_users = connection.prepareStatement("DELETE FROM users WHERE id = ?;")) {

                    statement_user_details.setInt(1, ID);
                    statement_users.setInt(1, user_id);


                    statement_users.executeUpdate();


                    connection.commit();

                    showAlert("Success", "User deleted successfully.", Alert.AlertType.INFORMATION);
                    loadUsersFromDatabase(userTable);
                } catch (SQLException e) {

                    connection.rollback();
                    showAlert("Error", "Failed to delete user: " + e.getMessage(), Alert.AlertType.ERROR);
                } finally {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete user: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }



    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

