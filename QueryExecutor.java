package com.netconnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class QueryExecutor {
    private static final String URL = "jdbc:mysql://localhost:3306/social_media_app";

    private static final String APP_USER = "app_user";
    private static final String APP_USER_PASSWORD = "User_password123?";  // credentials for the user side

    private static final String APP_ADMIN = "app_admin";
    private static final String APP_ADMIN_PASSWORD = "Admin_password123?"; // credentials for the admin


    private static final String SUPER_ADMIN = "super_admin";
    private static final String SUPER_ADMIN_PASSWORD = "Super_password123?"; // credntials for the cunty admin

    public Connection getAppUserConnection() {
        try {
            return DriverManager.getConnection(URL, APP_USER, APP_USER_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get connection for app_user: " + e.getMessage(), e);
        }
    }

    public Connection getAppAdminConnection() {
        try {
            return DriverManager.getConnection(URL, APP_ADMIN, APP_ADMIN_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get connection for app_admin: " + e.getMessage(), e);
        }
    }

    public Connection getSuperAdminConnection() {
        try {
            return DriverManager.getConnection(URL, SUPER_ADMIN, SUPER_ADMIN_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get connection for super_admin: " + e.getMessage(), e);
        }
    }
}
