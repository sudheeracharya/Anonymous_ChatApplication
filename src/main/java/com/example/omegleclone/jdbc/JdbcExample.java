package com.example.omegleclone.jdbc;

import java.sql.*;

public class JdbcExample {
    // Use environment variables or a secure properties file instead of hardcoded credentials
    private static final String URL = "jdbc:mysql://localhost:3306/omegle_clone";
    private static final String USER = System.getenv("root"); // Get from environment variable
    private static final String PASSWORD = System.getenv("Sudheer2852005"); // Get from environment variable

    public void performJoinOperation() {
        String query = "SELECT u.username, ui.interest FROM users u JOIN user_interests ui ON u.id = ui.user_id";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String username = rs.getString("username");
                String interest = rs.getString("interest");
                System.out.println(username + " is interested in " + interest);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        JdbcExample example = new JdbcExample();
        example.performJoinOperation();
    }
}


//
//package com.example.omegleclone.jdbc;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.HashMap;
//
//public class JdbcExample {
//    private static final String URL = "jdbc:mysql://localhost:3306/omegle_clone";
//    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
//    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "Sudheer2852005";
//
//    public String performJoinOperation() {
//        StringBuilder result = new StringBuilder();
//        String query = "SELECT u.id, u.username, u.interests AS user_interests, GROUP_CONCAT(ui.interest) AS joined_interests " +
//                "FROM users u " +
//                "LEFT JOIN user_interests ui ON u.id = ui.user_id " +
//                "GROUP BY u.id";
//
//        result.append("User ID | Username | Interests from users table | Interests from join table\n");
//        result.append("--------|----------|----------------------------|---------------------------\n");
//
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                Long userId = rs.getLong("id");
//                String username = rs.getString("username");
//                String userInterests = rs.getString("user_interests");
//                String joinedInterests = rs.getString("joined_interests");
//
//                result.append(String.format("%-8d | %-8s | %-28s | %s%n",
//                        userId, username,
//                        (userInterests != null ? userInterests : "None"),
//                        (joinedInterests != null ? joinedInterests : "None")));
//            }
//        } catch (SQLException e) {
//            result.append("Database error: ").append(e.getMessage());
//        }
//
//        return result.toString();
//    }
//
//    public String showTablesSummary() {
//        StringBuilder result = new StringBuilder();
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
//            // Count users
//            try (Statement stmt = conn.createStatement();
//                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
//                if (rs.next()) {
//                    result.append("Total users: ").append(rs.getInt(1)).append("\n");
//                }
//            }
//
//            // Count users with interests in users table
//            try (Statement stmt = conn.createStatement();
//                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE interests IS NOT NULL AND interests != ''")) {
//                if (rs.next()) {
//                    result.append("Users with interests in users table: ").append(rs.getInt(1)).append("\n");
//                }
//            }
//
//            // Count distinct users in user_interests table
//            try (Statement stmt = conn.createStatement();
//                 ResultSet rs = stmt.executeQuery("SELECT COUNT(DISTINCT user_id) FROM user_interests")) {
//                if (rs.next()) {
//                    result.append("Users with interests in user_interests table: ").append(rs.getInt(1)).append("\n");
//                }
//            }
//
//            // Count total interests in user_interests table
//            try (Statement stmt = conn.createStatement();
//                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user_interests")) {
//                if (rs.next()) {
//                    result.append("Total interests in user_interests table: ").append(rs.getInt(1)).append("\n");
//                }
//            }
//
//        } catch (SQLException e) {
//            result.append("Database error: ").append(e.getMessage());
//        }
//        return result.toString();
//    }
//
//    public String migrateInterestsToJoinTable() {
//        StringBuilder resultLog = new StringBuilder();
//        String selectQuery = "SELECT id, username, interests FROM users WHERE interests IS NOT NULL";
//
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             Statement selectStmt = conn.createStatement();
//             ResultSet rs = selectStmt.executeQuery(selectQuery)) {
//
//            String insertQuery = "INSERT IGNORE INTO user_interests (user_id, interest) VALUES (?, ?)";
//            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
//
//            int totalInserted = 0;
//
//            while (rs.next()) {
//                Long userId = rs.getLong("id");
//                String username = rs.getString("username");
//                String interestsStr = rs.getString("interests");
//
//                if (interestsStr != null && !interestsStr.trim().isEmpty()) {
//                    String[] interests = interestsStr.split(",");
//
//                    for (String interest : interests) {
//                        String trimmedInterest = interest.trim();
//                        if (!trimmedInterest.isEmpty()) {
//                            insertStmt.setLong(1, userId);
//                            insertStmt.setString(2, trimmedInterest);
//
//                            try {
//                                int rowsAffected = insertStmt.executeUpdate();
//                                totalInserted += rowsAffected;
//                                resultLog.append("Added interest '").append(trimmedInterest).append("' for user ").append(username).append("\n");
//                            } catch (SQLException e) {
//                                resultLog.append("Error adding interest '").append(trimmedInterest)
//                                        .append("' for user ").append(username).append(": ").append(e.getMessage()).append("\n");
//                            }
//                        }
//                    }
//                }
//            }
//
//            resultLog.append("Migration completed. Total interests inserted: ").append(totalInserted).append("\n");
//            insertStmt.close();
//
//        } catch (SQLException e) {
//            resultLog.append("Database error during migration: ").append(e.getMessage()).append("\n");
//        }
//        return resultLog.toString();
//    }
//
//    public static void main(String[] args) {
//        JdbcExample example = new JdbcExample();
//
//        System.out.println("Table Summary Before Migration:");
//        System.out.println(example.showTablesSummary());
//
//        System.out.println("\nMigrating interests to join table...");
//        System.out.println(example.migrateInterestsToJoinTable());
//
//        System.out.println("\nTable Summary After Migration:");
//        System.out.println(example.showTablesSummary());
//
//        System.out.println("\nJoin Operation Results:");
//        System.out.println(example.performJoinOperation());
//    }
//}