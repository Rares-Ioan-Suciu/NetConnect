package com.netconnect.Applications.UserApp.UserScenes.MenusLogin;

import com.netconnect.Applications.UserApp.Helpers.ProfileLoader;
import com.netconnect.Applications.UserApp.Helpers.SceneNavigator;
import com.netconnect.Applications.UserApp.UserApplication;
import com.netconnect.Applications.UserApp.Details.Email;
import com.netconnect.Applications.UserApp.Details.Password;
import com.netconnect.Applications.UserApp.User;
import com.netconnect.Applications.UserApp.Details.Username;
import com.netconnect.Applications.InterfacesAndParents.Sceneable;
import com.netconnect.QueryExecutor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class MatchScene implements Sceneable {

    private final SceneNavigator navigator;
    private final UserApplication userApplication;
    private final User currentUser;
    private final static ProfileLoader profileLoader = new ProfileLoader();
    private final static QueryExecutor queryExecutor = new QueryExecutor();

    public MatchScene(SceneNavigator navigator, UserApplication userApplication, User currentUser) {
        this.navigator = navigator;
        this.userApplication = userApplication;
        this.currentUser = currentUser;
    }

    public Scene getScene() {
        VBox layout = new VBox(10);

        List<User> bestMatches = findTopMatches();
        Label matchInfoLabel = new Label();
        matchInfoLabel.setText("default");

        if (bestMatches.getFirst() != null) {
            return new SeeProfileScene(navigator, currentUser, true, 0).getScene();
        } else {
            matchInfoLabel.setText("No suitable matches found.");
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> navigator.navigateTo(MainScreenScene.class));
        Button reportBug = new Button("Report Bug");
        reportBug.setOnAction(actionEvent -> navigator.navigateTo(ReportBugScene.class));

        layout.getChildren().addAll(matchInfoLabel, backButton, reportBug);
        return new Scene(layout, 900, 1000);
    }

    private List<User> findTopMatches() {
        List<User> topMatches = new ArrayList<>();
        PriorityQueue<Map.Entry<User, Integer>> topUsersQueue = new PriorityQueue<>(
                Map.Entry.comparingByValue()
        );

        String query = "SELECT username, email, password_hash FROM user_details WHERE username != ?";

        try (Connection connection = queryExecutor.getAppUserConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, currentUser.getUsername().getDetails());

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Username username = new Username(rs.getString("username"));
                    Email email = new Email(rs.getString("email"));
                    Password password = new Password(rs.getString("password_hash"));

                    User other = profileLoader.loadUserProfile(username, email, password);
                    int score = userApplication.match(other, currentUser);

                    if (topUsersQueue.size() < 10) {
                        topUsersQueue.offer(Map.entry(other, score));
                    } else if (score > topUsersQueue.peek().getValue()) {
                        topUsersQueue.poll();
                        topUsersQueue.offer(Map.entry(other, score));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error accessing the database: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", "Something went wrong while accessing the database. Please try again later.");
        }

        while (!topUsersQueue.isEmpty()) {
            topMatches.add(0, topUsersQueue.poll().getKey());
        }
        currentUser.setBestMatches(topMatches);
        return topMatches;
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
