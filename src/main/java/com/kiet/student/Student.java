package com.kiet.student;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Q1: Student class (Encapsulation: private fields + getters/setters)
public class Student {

    private int id;
    private String name;
    private String email;
    private String courseName;
    private double programmingScore;
    private double mathematicsScore;
    private List<String> technicalSkills;

    // Default constructor
    public Student() {
        this.technicalSkills = new ArrayList<>();
    }

    // Parameterized constructor
    public Student(int id, String name, String email, String courseName,
                   double programmingScore, double mathematicsScore, List<String> technicalSkills) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.courseName = courseName;
        this.programmingScore = programmingScore;
        this.mathematicsScore = mathematicsScore;
        this.technicalSkills = (technicalSkills == null) ? new ArrayList<>() : new ArrayList<>(technicalSkills);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public double getProgrammingScore() { return programmingScore; }
    public void setProgrammingScore(double programmingScore) { this.programmingScore = programmingScore; }

    public double getMathematicsScore() { return mathematicsScore; }
    public void setMathematicsScore(double mathematicsScore) { this.mathematicsScore = mathematicsScore; }

    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = (technicalSkills == null) ? new ArrayList<>() : new ArrayList<>(technicalSkills);
    }

    // ---------- Object methods ----------

    // Average of the two scores
    public double getAverage() {
        return (programmingScore + mathematicsScore) / 2.0;
    }

    // Performance category based on average
    public String getCategory() {
        double avg = getAverage();
        if (avg >= 85) return "Excellent";
        else if (avg >= 70) return "Good";
        else if (avg >= 50) return "Satisfactory";
        else return "Needs Improvement";
    }

    // Stronger subject
    public String getStrongerSubject() {
        if (programmingScore > mathematicsScore) return "Programming";
        else if (mathematicsScore > programmingScore) return "Mathematics";
        else return "Both equal";
    }

    // Add one skill
    public void addSkill(String skill) {
        technicalSkills.add(skill);
    }

    // Print full details
    public void displayDetails() {
        System.out.println("ID         : " + id);
        System.out.println("Name       : " + name);
        System.out.println("Email      : " + email);
        System.out.println("Course     : " + courseName);
        System.out.println("Programming: " + programmingScore);
        System.out.println("Mathematics: " + mathematicsScore);
        System.out.println("Skills     : " + technicalSkills);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return id == ((Student) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', email='" + email + "', course='" + courseName
                + "', programming=" + programmingScore + ", mathematics=" + mathematicsScore
                + ", skills=" + technicalSkills + "}";
    }
}