package com.unitrack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    private static final String DB_URL = "jdbc:sqlite:unitrack.db";

    public DBHelper() {
        createTables();
    }

    private void createTables() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Create tasks table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    description TEXT NOT NULL,
                    completed BOOLEAN DEFAULT 0
                );
            """);

            // Create subjects table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS subjects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    code TEXT
                );
            """);

            // Create timetable table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS timetable (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    subject_id INTEGER,
                    day TEXT,
                    time TEXT,
                    FOREIGN KEY(subject_id) REFERENCES subjects(id)
                );
            """);

            System.out.println("✅ Tables created or verified.");
        } catch (SQLException e) {
            System.out.println("❌ DB Error: " + e.getMessage());
        }
    }

    // ==== TASK METHODS ====

    public void addTask(String description) {
        String sql = "INSERT INTO tasks (description) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, description);
            pstmt.executeUpdate();
            System.out.println("✅ Task added: " + description);

        } catch (SQLException e) {
            System.out.println("❌ Error adding task: " + e.getMessage());
        }
    }

    public List<String> getTasks() {
        List<String> tasks = new ArrayList<>();
        String sql = "SELECT description FROM tasks";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(rs.getString("description"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error reading tasks: " + e.getMessage());
        }

        return tasks;
    }

    public void deleteTask(String description) {
        String sql = "DELETE FROM tasks WHERE description = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, description);
            pstmt.executeUpdate();
            System.out.println("🗑️ Task deleted: " + description);

        } catch (SQLException e) {
            System.out.println("❌ Error deleting task: " + e.getMessage());
        }
    }
}
