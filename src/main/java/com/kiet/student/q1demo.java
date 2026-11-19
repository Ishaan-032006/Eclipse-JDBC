package com.kiet.student;

import java.util.Arrays;

// Q1 ko dikhane ke liye chhota demo (database ki zarurat nahi)
public class q1demo {
    public static void main(String[] args) {
        Student s = new Student(1, "Rahul", "rahul@kiet.edu", "B.Tech CSE",
                90, 82, Arrays.asList("Java", "SQL"));

        s.displayDetails();
        s.addSkill("Python");

        System.out.println("\nAfter adding a skill: " + s.getTechnicalSkills());
        System.out.println("Average         : " + s.getAverage());
        System.out.println("Category        : " + s.getCategory());
        System.out.println("Stronger subject: " + s.getStrongerSubject());

        // Setter ka use
        s.setMathematicsScore(95);
        System.out.println("\nAfter setMathematicsScore(95)");
        System.out.println("Average         : " + s.getAverage());
        System.out.println("Stronger subject: " + s.getStrongerSubject());

        System.out.println("\n" + s);
    }
}