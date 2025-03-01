package com.netconnect.Applications.UserApp.Details;

import com.netconnect.Applications.UserApp.Exception.UsernameAlreadyExistsException;
import com.netconnect.QueryExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Username extends UserDetails {
    private static final String USERNAME_IS_UNIQUE = "SELECT username FROM user_details WHERE username = ?";
    private final QueryExecutor queryExecutor = new QueryExecutor();
    private final String username;

    public Username(String username) {
        this.username = username;
    }

    @Override
    public String getDetails() {
        return username != null ? username : "Username not provided";
    }

    @Override
    public void verifyInfo() throws UsernameAlreadyExistsException {
        if (!isUniqueUsername(username) || username.contains(",")) {
            throw new UsernameAlreadyExistsException("The username '" + username + "' is already in use or contains a comma.");
        }
        setVerified(true);
    }

    private boolean isUniqueUsername(String username) {
        try(Connection connection = queryExecutor.getAppUserConnection())
        {
            PreparedStatement uniqueQuery = connection.prepareStatement(USERNAME_IS_UNIQUE);
            uniqueQuery.setString(1, username.trim().toLowerCase());

            try (ResultSet resultSet = uniqueQuery.executeQuery()) {
                return !resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Some kind of error happened when checking username uniqueness.");
        }
    }

}
