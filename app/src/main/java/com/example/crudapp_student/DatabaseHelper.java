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
            try (Connection conn = getConnection()) {
                if (conn == null) {
                    callback.onComplete(null);
                    return;
                }
                String sql = "INSERT INTO students (name, grade) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, student.getName());
                    pstmt.setInt(2, student.getGrade());
                    boolean success = pstmt.executeUpdate() > 0;
                    callback.onComplete(success);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onComplete(null);
            }
        });
    }

    public static void getAllStudents(Callback<List<Student>> callback) {
        executor.execute(() -> {
            try (Connection conn = getConnection()) {
                if (conn == null) {
                    callback.onComplete(null);
                    return;
                }
                List<Student> list = new ArrayList<>();
                // 修正済み：すべての学生を取得
                String sql = "SELECT * FROM students";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        list.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getInt("grade")));
                    }
                }
                callback.onComplete(list);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onComplete(null);
            }
        });
    }

    public static void updateStudent(Student student, Callback<Boolean> callback) {
        executor.execute(() -> {
            try (Connection conn = getConnection()) {
                if (conn == null) {
                    callback.onComplete(null);
                    return;
                }
                String sql = "UPDATE students SET name = ?, grade = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, student.getName());
                    // 修正済み：正しい学年を保存
                    pstmt.setInt(2, student.getGrade());
                    pstmt.setInt(3, student.getId());
                    boolean success = pstmt.executeUpdate() > 0;
                    callback.onComplete(success);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onComplete(null);
            }
        });
    }

    public static void deleteStudent(int id, Callback<Boolean> callback) {
        executor.execute(() -> {
            try (Connection conn = getConnection()) {
                if (conn == null) {
                    callback.onComplete(null);
                    return;
                }
                String sql = "DELETE FROM students WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    boolean success = pstmt.executeUpdate() > 0;
                    callback.onComplete(success);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onComplete(null);
            }
        });
    }
}
