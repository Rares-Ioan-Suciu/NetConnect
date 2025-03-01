package com.netconnect.Applications.UserApp.Details;

import com.netconnect.Applications.UserApp.Exception.InvalidEmailException;
import com.netconnect.QueryExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class Email extends UserDetails {
    private final String email;
    private static final String EMAIL_IS_UNIQUE = "SELECT email FROM user_details WHERE email = ?";
    private static final QueryExecutor queryExecutor = new QueryExecutor();
    private static final Set<String> ALLOWED_DOMAINS = Set.of("gmail.com", "yahoo.com", "outlook.com", "e-uvt.ro");

    public Email(String email) {
        this.email = email != null ? email.toLowerCase() : "";
    }

    public static void setQueryExecutor(QueryExecutor queryExecutorMock) {
    }

    @Override
    public String getDetails() {
        return email != null ? email : "Email not provided";
    }

    @Override
    public void verifyInfo() throws InvalidEmailException {
        if (!isValidEmailFormat() || !hasValidDomain()) {
            throw new InvalidEmailException("Invalid email format or domain.");
        }

        try (Connection connection = queryExecutor.getAppUserConnection();
             PreparedStatement emailUnique = connection.prepareStatement(EMAIL_IS_UNIQUE)) {

            emailUnique.setString(1, email);

            try (ResultSet rs = emailUnique.executeQuery()) {
                setVerified(!rs.next());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during email verification", e);
        }
    }

    private boolean isValidEmailFormat() {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private boolean hasValidDomain() {
        String domain = email.substring(email.indexOf("@") + 1);
        return ALLOWED_DOMAINS.contains(domain);
    }
}

