package com.netconnect.Applications.UserApp.Helpers;

import com.netconnect.Applications.UserApp.Details.*;
import com.netconnect.Applications.UserApp.User;
import com.netconnect.Applications.UserApp.UserScenes.SignUpUpdateProcess.QuestionsScene;
import com.netconnect.QueryExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileLoader {

    private static final QueryExecutor queryExecutor = new QueryExecutor();
    private static final String queryLoadAll = "SELECT u.* FROM users u JOIN user_details ud ON u.id = ud.user_id WHERE ud.username = ?;";

    public User loadUserProfile(Username username, Email email, Password password) {
        NameDetails nameDetails = null;
        String genderDetails = null;
        DateOfBirthDetails dateOfBirthDetails = null;
        List<String> interests = new ArrayList<>();
        List<String> abilities = new ArrayList<>();
        List<String> languages = new ArrayList<>();
        List<String> expertise = new ArrayList<>();
        List<String> education = new ArrayList<>();
        Question question = null;
        String imagePath = null;

        try (Connection connection = queryExecutor.getAppUserConnection()) {
            PreparedStatement loadStatement = connection.prepareStatement(queryLoadAll);
            loadStatement.setString(1, username.getDetails());

            try (ResultSet resultSet = loadStatement.executeQuery()) {
                while (resultSet.next()) {
                    nameDetails = new NameDetails(resultSet.getString("full_name"));
                    genderDetails = resultSet.getString("gender");
                    dateOfBirthDetails = new DateOfBirthDetails(resultSet.getString("date_of_birth"));

                    interests = parseList(resultSet.getString("interests"));
                    abilities = parseList(resultSet.getString("abilities"));
                    languages = parseList(resultSet.getString("programming_languages"));
                    education = parseList(resultSet.getString("education"));
                    expertise = parseList(resultSet.getString("domain_of_expertise"));

                    question = loadQuestion(resultSet.getString("question"));

                    imagePath = resultSet.getString("profile_picture_url");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Something went wrong loading the profile: ", e);
        }

        if (nameDetails == null || genderDetails == null || dateOfBirthDetails == null) {
            System.out.println("Missing required profile details for user: " + username.getDetails());
        }

        return new User(username, email, password, nameDetails, genderDetails, dateOfBirthDetails, interests, abilities, languages, expertise, education, question, imagePath);
    }

    private List<String> parseList(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(input.split("\\s*,\\s*"));
    }

    private Question loadQuestion(String questionColumn) {
        if (questionColumn == null || questionColumn.isEmpty()) {
            return null;
        }

        String[] parts = questionColumn.split(":\\s*", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid question format: " + questionColumn);
        }

        String categoryName = parts[0].trim();
        String questionText = parts[1].trim();

        int categoryId = getCategoryId(categoryName);
        return QuestionsScene.getQuestion(categoryId, questionText);
    }

    private int getCategoryId(String categoryName) {

        String processedCategoryName = categoryName.replaceFirst("^\\d+\\.\\s*", "");

        String query = "SELECT category_id FROM categories WHERE category_name = ?";
        try (Connection connection = queryExecutor.getAppUserConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, processedCategoryName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("category_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching category ID: " + e.getMessage());
        }
        throw new IllegalArgumentException("Invalid category name: " + categoryName);
    }


}
