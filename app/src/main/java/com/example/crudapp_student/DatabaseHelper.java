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
        getStudentsFiltered("", 0, "id", callback);
    }

    public static void getStudentsFiltered(String query, String sortBy, Callback<List<Student>> callback) {
        getStudentsFiltered(query, 0, sortBy, callback);
    }

    public static void getStudentsFiltered(String query, int gradeFilter, String sortBy, Callback<List<Student>> callback) {
        executor.execute(() -> {
            try (Connection conn = getConnection()) {
                if (conn == null) {
                    callback.onComplete(null);
                    return;
                }

                String orderByClause = "id";
                if ("name".equals(sortBy)) {
                    orderByClause = "name";
                }

                List<Student> list = new ArrayList<>();
                // バグ仕込み3：学年フィルターの条件が >= になっている（「2年生」を選んでも3年生まで出る）
                StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE name LIKE ? ");
                if (gradeFilter > 0) {
                    sql.append("AND grade >= ? ");
                }
                sql.append("ORDER BY ").append(orderByClause);

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    pstmt.setString(1, "%" + query + "%");
                    if (gradeFilter > 0) {
                        pstmt.setInt(2, gradeFilter);
                    }
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            list.add(new Student(rs.getInt("id"), rs.getString("name"), rs.getInt("grade")));
                        }
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

    public static void deleteAllStudents(Callback<Boolean> callback) {
        executor.execute(() -> {
            try (Connection conn = getConnection()) {
                if (conn == null) {
                    callback.onComplete(null);
                    return;
                }
                String sql = "DELETE FROM students";
                try (Statement stmt = conn.createStatement()) {
                    boolean success = stmt.executeUpdate(sql) >= 0;
                    callback.onComplete(success);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onComplete(null);
            }
        });
    }
}
