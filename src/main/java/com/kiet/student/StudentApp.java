package com.kiet.student;

import java.sql.SQLException;
import java.util.*;

// Q2: Menu-driven application. Database se data DAO deta hai, logic yahan collections se hota hai.
public class StudentApp {

    static Scanner sc = new Scanner(System.in);
    static StudentDAO dao = new StudentDAO();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== STUDENT PROFILE & SKILL MANAGEMENT =====");
            System.out.println("1. Add Student");
            System.out.println("2. Show All Students (Average, Category, Stronger Subject)");
            System.out.println("3. Search Student (ID / Email / Course)");
            System.out.println("4. Merit List");
            System.out.println("5. Students with Same Technical Skills");
            System.out.println("6. Needs Improvement in Programming (Maths >= 75)");
            System.out.println("7. Insert Sample Data");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1": addStudent(); break;
                    case "2": showAll(); break;
                    case "3": search(); break;
                    case "4": meritList(); break;
                    case "5": sameSkills(); break;
                    case "6": needsImprovement(); break;
                    case "7": insertSampleData(); break;
                    case "8":
                        System.out.println("Exiting...");
                        return;
                    default: System.out.println("Invalid choice!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number entered.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());     // validation / duplicate
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    // 8. Add student with validation
    static void addStudent() throws SQLException {
        System.out.print("Student ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Course: ");
        String course = sc.nextLine();
        System.out.print("Programming score (0-100): ");
        double prog = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Mathematics score (0-100): ");
        double math = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Technical skills (comma separated): ");
        String skillLine = sc.nextLine();

        List<String> skills = new ArrayList<>();
        for (String sk : skillLine.split(",")) {
            if (!sk.trim().isEmpty()) skills.add(sk.trim());
        }

        dao.addStudent(new Student(id, name, email, course, prog, math, skills));
        System.out.println("Student added successfully.");
    }

    // 1, 2, 3. Average, category, stronger subject for each student
    static void showAll() throws SQLException {
        List<Student> list = dao.getAllStudents();
        if (list.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.printf("%-4s %-12s %-6s %-6s %-8s %-18s %-12s %s%n",
                "ID", "Name", "Prog", "Math", "Average", "Category", "Stronger", "Skills");
        for (Student s : list) {
            System.out.printf("%-4d %-12s %-6.1f %-6.1f %-8.2f %-18s %-12s %s%n",
                    s.getId(), s.getName(), s.getProgrammingScore(), s.getMathematicsScore(),
                    s.getAverage(), s.getCategory(), s.getStrongerSubject(), s.getTechnicalSkills());
        }
    }

    // 4. Search by ID, email or course
    static void search() throws SQLException {
        System.out.println("1. By ID  2. By Email  3. By Course");
        System.out.print("Enter option: ");
        String opt = sc.nextLine().trim();
        List<Student> result;

        if (opt.equals("1")) {
            System.out.print("Enter ID: ");
            result = dao.searchById(Integer.parseInt(sc.nextLine().trim()));
        } else if (opt.equals("2")) {
            System.out.print("Enter email: ");
            result = dao.searchByEmail(sc.nextLine().trim());
        } else if (opt.equals("3")) {
            System.out.print("Enter course: ");
            result = dao.searchByCourse(sc.nextLine().trim());
        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (result.isEmpty()) {
            System.out.println("No student found.");
        } else {
            for (Student s : result) {
                System.out.println("-----------------------------");
                s.displayDetails();
            }
        }
    }

    // 5. Merit list: average descending (List + Comparator)
    static void meritList() throws SQLException {
        List<Student> list = dao.getAllStudents();
        list.sort((a, b) -> {
            int c = Double.compare(b.getAverage(), a.getAverage());   // descending
            return (c != 0) ? c : a.getName().compareTo(b.getName());
        });

        System.out.printf("%-5s %-4s %-12s %-8s %s%n", "Rank", "ID", "Name", "Average", "Category");
        int rank = 1;
        for (Student s : list) {
            System.out.printf("%-5d %-4d %-12s %-8.2f %s%n",
                    rank++, s.getId(), s.getName(), s.getAverage(), s.getCategory());
        }
    }

    // 6. Students having the same technical skill (Map + Set)
    static void sameSkills() throws SQLException {
        Map<String, List<String>> skillMap = new TreeMap<>();     // skill -> student names

        for (Student s : dao.getAllStudents()) {
            Set<String> unique = new HashSet<>();                 // duplicate skill hatane ke liye
            for (String skill : s.getTechnicalSkills()) {
                unique.add(skill.trim().toLowerCase());
            }
            for (String skill : unique) {
                skillMap.computeIfAbsent(skill, k -> new ArrayList<>()).add(s.getName());
            }
        }

        boolean found = false;
        for (Map.Entry<String, List<String>> e : skillMap.entrySet()) {
            if (e.getValue().size() >= 2) {                       // 2 ya zyada students
                System.out.println(e.getKey() + " -> " + e.getValue());
                found = true;
            }
        }
        if (!found) System.out.println("No common skills found.");
    }

    // 7. Programming below 50 but mathematics at least 75
    static void needsImprovement() throws SQLException {
        boolean found = false;
        for (Student s : dao.getAllStudents()) {
            if (s.getProgrammingScore() < 50 && s.getMathematicsScore() >= 75) {
                System.out.println(s.getId() + " | " + s.getName() + " | Programming: "
                        + s.getProgrammingScore() + " | Mathematics: " + s.getMathematicsScore());
                found = true;
            }
        }
        if (!found) System.out.println("No such students found.");
    }

    // Testing ke liye sample data
    static void insertSampleData() throws SQLException {
        Student[] samples = {
            new Student(1, "Rahul", "rahul@kiet.edu", "B.Tech CSE", 90, 82, Arrays.asList("Java", "SQL")),
            new Student(2, "Priya", "priya@kiet.edu", "B.Tech CSE", 72, 68, Arrays.asList("Java", "Python")),
            new Student(3, "Amit",  "amit@kiet.edu",  "B.Tech IT",  45, 80, Arrays.asList("Excel", "SQL")),
            new Student(4, "Neha",  "neha@kiet.edu",  "B.Tech IT",  40, 35, Arrays.asList("HTML"))
        };
        for (Student s : samples) {
            try {
                dao.addStudent(s);
                System.out.println("Inserted: " + s.getName());
            } catch (IllegalArgumentException e) {
                System.out.println("Skipped " + s.getName() + ": " + e.getMessage());
            }
        }
    }
}