package com.netconnect.Applications.UserApp.Helpers;

import com.netconnect.QueryExecutor;

import java.io.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserProfileService {

    private final static QueryExecutor queryExecutor = new QueryExecutor();
    private final static String updateQuery = "UPDATE users u " +
            "JOIN user_details ud ON ud.user_id = u.id " +
            "SET u.full_name = ?, u.gender = ?, u.date_of_birth = ? " +
            "WHERE ud.username = ?";
    private final static String updateGeneralQuery = "UPDATE users u " +
            "JOIN user_details ud ON ud.user_id = u.id " +
            "SET u.interests = ?, u.abilities = ?, u.programming_languages = ?, u.domain_of_expertise = ?, u.education = ? " +
            "WHERE ud.username = ?";

    public void updateUserDetails(String username, String fullName, String gender, String dateOfBirth) {
        try (Connection connection = queryExecutor.getAppUserConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, gender);
            preparedStatement.setString(3, dateOfBirth);
            preparedStatement.setString(4, username);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0)
                throw new SQLException("Failed to update user details");
            System.out.println("Profile updated successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Error in updating user personal details: " + e.getMessage());
        }
    }



    public void updateGeneralDetails(String username, List<String> interests, List<String> abilities, List<String> languages, List<String> expertise, List<String> education) {

        try (Connection connection = queryExecutor.getAppUserConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateGeneralQuery)) {
            preparedStatement.setString(1, String.join(", ", interests));
            preparedStatement.setString(2, String.join(", ", abilities));
            preparedStatement.setString(3, String.join(", ", languages));
            preparedStatement.setString(4, String.join(", ", expertise));
            preparedStatement.setString(5, String.join(", ", education));
            preparedStatement.setString(6, username);
            System.out.println(username);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update user details");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in updating user general interests/details: " + e.getMessage());
        }

    }


    public void updateQuestions(String username, String question) {
        String updateQuery = "UPDATE users u " +
                "JOIN user_details ud ON ud.user_id = u.id " +
                "SET u.question = ? " +
                "WHERE ud.username = ?";
        try (Connection connection = queryExecutor.getAppUserConnection(); PreparedStatement insertQuestion = connection.prepareStatement(updateQuery)) {
            insertQuestion.setString(1, question);
            insertQuestion.setString(2, username);
            int rowsUpdated = insertQuestion.executeUpdate();
            if(rowsUpdated == 0)
            {
                throw new SQLException("Failed to update question");
            }
        } catch (SQLException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }


    public void updateProfilePicture(File file, String username) {

        String profilePicturePath = "Users/" + username + "_profile_picture" + getFileExtension(file.getName());

        try {

            Files.copy(file.toPath(), Paths.get(profilePicturePath), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Profile picture uploaded successfully!");


            updateProfilePictureInDatabase(username, profilePicturePath);
        } catch (IOException e) {
            System.out.println("Error uploading profile picture: " + e.getMessage());
        }
    }

    private void updateProfilePictureInDatabase(String username, String profilePicturePath) {
        String updateQuery = "UPDATE users u " +
                "JOIN user_details ud ON ud.user_id = u.id " +
                "SET u.profile_picture_url = ? " +
                "WHERE ud.username = ?";

        try (Connection connection = queryExecutor.getAppUserConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, profilePicturePath);
            preparedStatement.setString(2, username);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Failed to update profile picture in database");
            }
            System.out.println("Profile picture path updated in database successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating profile picture in database: " + e.getMessage());
        }
    }


    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
}
