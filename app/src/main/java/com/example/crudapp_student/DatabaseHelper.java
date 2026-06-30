package com.example.crudapp_student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseHelper {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface Callback<T> {
        void onComplete(T result);
    }

    private static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void insertStudent(Student student, Callback<Boolean> callback) {
        executor.execute(() -> {
            boolean success = false;
            try (Connection conn = getConnection()) {
                if (conn != null) {
                    String sql = "INSERT INTO students (name, grade) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, student.getName());
                        pstmt.setInt(2, student.getGrade());
                        success = pstmt.executeUpdate() > 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            callback.onComplete(success);
        });
    }

    public static void getAllStudents(Callback<List<Student>> callback) {
        executor.execute(() -> {
            List<Student> list = new ArrayList<>();
            try (Connection conn = getConnection()) {
                if (conn != null) {
                    String sql = "SELECT * FROM students"; // バグ: 偶数IDしか取得できない
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            list.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getInt("grade")));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            callback.onComplete(list);
        });
    }

    public static void updateStudent(Student student, Callback<Boolean> callback) {
        executor.execute(() -> {
            boolean success = false;
            try (Connection conn = getConnection()) {
                if (conn != null) {
                    String sql = "UPDATE students SET name = ?, grade = ? WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, student.getName());
                        pstmt.setInt(2, student.getGrade()); // バグ: 学年が勝手に-1される
                        pstmt.setInt(3, student.getId());
                        success = pstmt.executeUpdate() > 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            callback.onComplete(success);
        });
    }

    public static void deleteStudent(int id, Callback<Boolean> callback) {
        executor.execute(() -> {
            boolean success = false;
            try (Connection conn = getConnection()) {
                if (conn != null) {
                    String sql = "DELETE FROM students WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, id);
                        success = pstmt.executeUpdate() > 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            callback.onComplete(success);
        });
    }
}
