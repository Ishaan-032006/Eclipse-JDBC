package com.kiet.student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Database ka saara kaam yahan (JDBC). Business logic StudentApp me hai.
public class StudentDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Ishan@23#4";   // <-- apna password dalo

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ---------- Add student (duplicate ID check + score validation) ----------
    public void addStudent(Student s) throws SQLException {
        // Validation
        if (s.getId() <= 0)
            throw new IllegalArgumentException("Student ID must be positive.");
        if (s.getName() == null || s.getName().trim().isEmpty()
                || s.getEmail() == null || s.getEmail().trim().isEmpty())
            throw new IllegalArgumentException("Name and email are required.");
        if (s.getProgrammingScore() < 0 || s.getProgrammingScore() > 100
                || s.getMathematicsScore() < 0 || s.getMathematicsScore() > 100)
            throw new IllegalArgumentException("Scores must be between 0 and 100.");

        String insertStudent = "INSERT INTO student (id, name, email, course_name, "
                + "programming_score, mathematics_score) VALUES (?, ?, ?, ?, ?, ?)";
        String insertSkill = "INSERT INTO student_skills (student_id, skill) VALUES (?, ?)";

        Connection con = null;
        try {
            con = getConnection();

            if (exists(con, "SELECT 1 FROM student WHERE id = ?", s.getId()))
                throw new IllegalArgumentException("Duplicate student ID: " + s.getId());
            if (exists(con, "SELECT 1 FROM student WHERE LOWER(email) = LOWER(?)", s.getEmail()))
                throw new IllegalArgumentException("Email already exists: " + s.getEmail());

            con.setAutoCommit(false);       // transaction: student + skills ek saath save honge

            try (PreparedStatement ps = con.prepareStatement(insertStudent)) {
                ps.setInt(1, s.getId());
                ps.setString(2, s.getName());
                ps.setString(3, s.getEmail());
                ps.setString(4, s.getCourseName());
                ps.setDouble(5, s.getProgrammingScore());
                ps.setDouble(6, s.getMathematicsScore());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(insertSkill)) {
                for (String skill : s.getTechnicalSkills()) {
                    ps.setInt(1, s.getId());
                    ps.setString(2, skill.trim());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) con.close();
        }
    }

    // ---------- Retrieve ----------
    public List<Student> getAllStudents() throws SQLException {
        return query("SELECT * FROM student ORDER BY id", null);
    }

    public List<Student> searchById(int id) throws SQLException {
        return query("SELECT * FROM student WHERE id = ?", id);
    }

    public List<Student> searchByEmail(String email) throws SQLException {
        return query("SELECT * FROM student WHERE LOWER(email) = LOWER(?)", email);
    }

    public List<Student> searchByCourse(String course) throws SQLException {
        return query("SELECT * FROM student WHERE LOWER(course_name) = LOWER(?)", course);
    }

    // ---------- Helpers ----------
    private boolean exists(Connection con, String sql, Object param) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private List<Student> query(String sql, Object param) throws SQLException {
        List<Student> list = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (param != null) ps.setObject(1, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setId(rs.getInt("id"));
                    s.setName(rs.getString("name"));
                    s.setEmail(rs.getString("email"));
                    s.setCourseName(rs.getString("course_name"));
                    s.setProgrammingScore(rs.getDouble("programming_score"));
                    s.setMathematicsScore(rs.getDouble("mathematics_score"));
                    list.add(s);
                }
            }
            // Har student ke skills load karo
            for (Student s : list) {
                loadSkills(con, s);
            }
        }
        return list;
    }

    private void loadSkills(Connection con, Student s) throws SQLException {
        String sql = "SELECT skill FROM student_skills WHERE student_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getId());
            try (ResultSet rs = ps.executeQuery()) {
                List<String> skills = new ArrayList<>();
                while (rs.next()) {
                    skills.add(rs.getString("skill"));
                }
                s.setTechnicalSkills(skills);
            }
        }
    }
}