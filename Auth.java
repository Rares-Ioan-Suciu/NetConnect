package com.netconnect.Applications.UserApp.Helpers;

import com.netconnect.Applications.UserApp.Details.Email;
import com.netconnect.Applications.UserApp.Details.Password;
import com.netconnect.Applications.UserApp.Details.Username;
import com.netconnect.Applications.UserApp.User;
import com.netconnect.GoogleLoginHelper;
import com.netconnect.QueryExecutor;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth {

    private final QueryExecutor queryExecutor = new QueryExecutor();
    private static final String signUpInsert = "INSERT INTO user_details (username, email, password_hash, user_id, banned, is_finished) VALUES (?, ?, ?, ?, 0, 0)";
    private static final String loginLook = "SELECT email, password_hash, banned FROM user_details WHERE username = ?";
    private static final String createPlaceHolder = "INSERT INTO users (full_name, gender, date_of_birth, interests, abilities, programming_languages, " +
            "domain_of_expertise, education, question, profile_picture_url) " +
            "VALUES ('Placeholder User', 'Other', '2001-10-10', NULL, NULL, NULL, NULL, NULL, NULL, NULL);";
    private static int diagnosis = 0;

    public static int getDiagnosis() {
        return diagnosis;
    }

    public static void setDiagnosis(int diagnosis) {
        Auth.diagnosis = diagnosis;
    }

    public User login(Username username, String password) {
        ProfileLoader profileLoader = new ProfileLoader();

        try (Connection connection = queryExecutor.getAppUserConnection()) {
            PreparedStatement loginStatement = connection.prepareStatement(loginLook);
            loginStatement.setString(1, username.getDetails());

            try (ResultSet rs = loginStatement.executeQuery()) {
                if (rs.next()) {
                    String storedEmail = rs.getString("email");
                    String storedHash = rs.getString("password_hash");
                    int banned = rs.getInt("banned");

                    if(banned == 1)
                    {
                        setDiagnosis(1);
                        return null;
                    }


                    if (BCrypt.checkpw(password, storedHash)) {
                        System.out.println("Password matches successfully.");
                        Email email = new Email(storedEmail);
                        Password pass = new Password(storedHash);

                        return profileLoader.loadUserProfile(username, email, pass);
                    } else {
                        System.out.println("Password mismatch for user: " + username.getDetails());
                        System.out.println("Input password: " + password);
                        System.out.println("Stored hash: " + storedHash);
                    }
                } else {
                    setDiagnosis(2);
                    return null;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Something went wrong when trying to log in", ex);
        }
        setDiagnosis(3);
        return null;
    }

    public User signUp(Username username, Email email, Password password) {
        try (Connection connection = queryExecutor.getAppUserConnection()) {
            PreparedStatement createUserStatement = connection.prepareStatement(createPlaceHolder, PreparedStatement.RETURN_GENERATED_KEYS);
            int rowsAffected = createUserStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = createUserStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    PreparedStatement signUpStatement = connection.prepareStatement(signUpInsert);
                    signUpStatement.setString(1, username.getDetails());
                    signUpStatement.setString(2, email.getDetails());

                    String hashedPassword = password.getDetails();
                    signUpStatement.setString(3, hashedPassword);

                    signUpStatement.setInt(4, userId);

                    int detailsRowsAffected = signUpStatement.executeUpdate();

                    if (detailsRowsAffected > 0) {
                        System.out.println("Sign-up successful for user: " + username.getDetails());
                        return new User(username, email, new Password(hashedPassword));
                    } else {
                        System.out.println("Failed to insert user details for: " + username.getDetails());
                    }
                }
            } else {
                System.out.println("Failed to create a placeholder user in the `users` table.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Something went wrong while signing up: " + e.getMessage(), e);
        }
        return null;
    }


    public User googleLogin(GoogleLoginHelper.GoogleUser googleUser) {
        try (Connection connection = queryExecutor.getAppUserConnection()) {
            String findUserQuery = "SELECT username, email, password_hash, banned FROM user_details WHERE email = ?";
            PreparedStatement findUserStmt = connection.prepareStatement(findUserQuery);
            findUserStmt.setString(1, googleUser.getEmail());

            try (ResultSet rs = findUserStmt.executeQuery()) {
                if (rs.next()) {
                    int banned = rs.getInt("banned");
                    if(banned != 1) {
                        Username username = new Username(rs.getString("username"));
                        Email email = new Email(rs.getString("email"));
                        Password password = new Password(rs.getString("password_hash"));
                        ProfileLoader profileLoader = new ProfileLoader();
                        return profileLoader.loadUserProfile(username, email, password);
                    }
                    else
                    {
                        setDiagnosis(1);
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error during Google login: " + e.getMessage(), e);
        }
    }

    public QueryExecutor getQueryExecutor()
    {
        return queryExecutor;
    }

    public User googleSignUp(GoogleLoginHelper.GoogleUser googleUser) {
        try (Connection connection = queryExecutor.getAppUserConnection()) {

            String userInsertQuery = "INSERT INTO users (full_name, gender, date_of_birth, interests, abilities, programming_languages, " +
                    "domain_of_expertise, education, question, profile_picture_url) " +
                    "VALUES (?, ?, ?, NULL, NULL, NULL, NULL, NULL, NULL, ?);";

            PreparedStatement createUserStmt = connection.prepareStatement(userInsertQuery, PreparedStatement.RETURN_GENERATED_KEYS);

            createUserStmt.setString(1, googleUser.getName() != null ? googleUser.getName() : "Placeholder User");
            createUserStmt.setString(2, googleUser.getGender() != null ? googleUser.getGender() : "Other");
            createUserStmt.setString(3, googleUser.getBirthday() != null ? googleUser.getBirthday() : "2000-01-01");
            createUserStmt.setString(4, googleUser.getProfilePicture() != null ? googleUser.getProfilePicture() : null);

            createUserStmt.executeUpdate();

            ResultSet generatedKeys = createUserStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);


                String userDetailsInsertQuery = "INSERT INTO user_details (username, email, password_hash, user_id, banned, is_finished) " +
                        "VALUES (?, ?, ?, ?, 0, 0)";
                PreparedStatement signUpStmt = connection.prepareStatement(userDetailsInsertQuery);

                signUpStmt.setString(1, googleUser.getUsername());
                signUpStmt.setString(2, googleUser.getEmail());
                signUpStmt.setString(3, BCrypt.hashpw("GoogleAuth1?", BCrypt.gensalt()));
                signUpStmt.setInt(4, userId);

                signUpStmt.executeUpdate();

                return new User(
                        new Username(googleUser.getUsername()),
                        new Email(googleUser.getEmail()),
                        new Password("GoogleAuth1?")
                );
            } else {
                throw new RuntimeException("Failed to create user during Google sign-up.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error during Google sign-up: " + e.getMessage(), e);
        }
    }


}
