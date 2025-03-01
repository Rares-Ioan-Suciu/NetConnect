package com.netconnect;

import com.netconnect.Applications.AdminApp.AdminApplication;
import com.netconnect.Applications.HelpApp.HelpApplication;
import com.netconnect.Applications.SuperAdminApp.SuperAdminApp;
import com.netconnect.Applications.UserApp.UserApplication;
import com.netconnect.ChatRoom.ChatServer;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {

    private static String mode;

    @Override
    public void start(Stage stage) throws ArgumentException {

        cleanUp(); // before anything we clean up the unfinished accounts
        try {
            if (mode.equalsIgnoreCase("admin")) {
                launchAdminMode(stage);
            } else if (mode.equalsIgnoreCase("user")) {
                launchUserMode(stage);
            } else if (mode.equalsIgnoreCase("super_admin")) {
                launchSuperAdminMode(stage);
            } else if (mode.equalsIgnoreCase("help")) {
                launchHelpMode(stage);
            } else if (mode.equalsIgnoreCase("server")) {
                launchServerMode();
            } else {
                throw new ArgumentException("Invalid mode. Please use the application with one of the four arguments: help, admin, super_admin or user");
            }
        } catch (ArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Argument Input Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String @NotNull [] args) {
        if (args.length > 0) {
            mode = args[0];
        }
        launch(args);
    }

    private void launchUserMode(Stage stage) {
        UserApplication userApp = new UserApplication(stage);
        userApp.showMenu();
    }

    private void launchAdminMode(Stage stage) {
        AdminApplication adminApp = new AdminApplication(stage);
        adminApp.showMenu();
    }

    private void launchHelpMode(Stage stage) {
        HelpApplication helpApp = new HelpApplication(stage);
        helpApp.showMenu();
    }

    private void launchSuperAdminMode(Stage stage) {
        SuperAdminApp superAdminApp = new SuperAdminApp(stage);
        superAdminApp.showMenu();
    }

    private void launchServerMode() {
        Thread serverThread = new Thread(() -> {
            ChatServer chatServer = new ChatServer(12345, 8080);
            chatServer.startServer();
        });
        serverThread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted while waiting for server to start: " + e.getMessage());
        }
    }

    private void cleanUp() {
        final QueryExecutor queryExecutor = new QueryExecutor();
        try (Connection connection = queryExecutor.getSuperAdminConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statementGetUserIDs = connection.prepareStatement(
                    "SELECT user_id, id FROM user_details WHERE is_finished = 0;");
                 PreparedStatement statementDeleteUserDetails = connection.prepareStatement(
                         "DELETE FROM user_details WHERE id = ?;");
                 PreparedStatement statementDeleteUsers = connection.prepareStatement(
                         "DELETE FROM users WHERE id = ?;")) {
                try (ResultSet resultSet = statementGetUserIDs.executeQuery()) {
                    while (resultSet.next()) {
                        int user_id = resultSet.getInt("user_id");
                        int id = resultSet.getInt("id");
                        statementDeleteUserDetails.setInt(1, id);
                        statementDeleteUserDetails.executeUpdate();
                        statementDeleteUsers.setInt(1, user_id);
                        statementDeleteUsers.executeUpdate();
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
