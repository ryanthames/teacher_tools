package com.ocsoftware.teachertools.grad;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GradSeatingChartGenerator {
  private static final int SIDE_A_ROWS = 25;
  private static final int SIDE_B_ROWS = 26;

  private static final List<Student> students = new ArrayList<>();

  public static void main(String... args) {
    try {
      readDataFromFile();
      generateSeatingChart();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void generateSeatingChart() throws IOException {
    List<Student> filteredStudents = students
        .stream()
        .filter(GradSeatingChartGenerator::shouldNotRemove)
        .collect(Collectors.toList());

    int currentIndex = 0;
    try(BufferedWriter writer = new BufferedWriter(new FileWriter("side_a.csv"))) {
      boolean firstRow = true;

      // loop through side A
      for(int i = 0; i < SIDE_A_ROWS; i++) {
        int rowSize;

        if(firstRow) {
          firstRow = false;
          rowSize = 8;
        } else {
          rowSize = 10;
        }

        List<Student> studentRow = filteredStudents.subList(currentIndex, currentIndex + rowSize);
        Collections.reverse(studentRow);

        for(Student s : studentRow) {
          writer.write(String.format("%s,", s.getFirstMiddleName().charAt(0) + ". " + s.getLastName()));
        }

        writer.write("\n");

        currentIndex += rowSize;
      }
    }

    // loop through side B until you run out of students
    try(BufferedWriter writer = new BufferedWriter(new FileWriter("side_b.csv"))) {
      for(int i = 0; i < SIDE_B_ROWS; i++) {
        int rowSize = 10;

        List<Student> studentRow = filteredStudents.subList(currentIndex, currentIndex + rowSize);
        Collections.reverse(studentRow);

        for(Student s : studentRow) {
          writer.write(String.format("%s,", s.getFirstMiddleName().charAt(0) + ". " + s.getLastName()));
        }

        writer.write("\n");

        currentIndex += rowSize;

        if(currentIndex > filteredStudents.size()) {
          break;
        }
      }
    }
  }

  private static void readDataFromFile() throws IOException {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader("input.csv"))) {
      // ignore header
      bufferedReader.readLine();

      String line;
      while ((line = bufferedReader.readLine()) != null) {
//        System.out.println(line);
        String[] tokens = line.split(",");
        if (tokens.length != 0) {
          students.add(new Student(tokens[0], tokens[1], tokens.length > 2 ? tokens[2] : ""));
        }
      }
    }
  }

  private static boolean shouldNotRemove(Student student) {
    return !shouldRemove(student);
  }

  private static boolean shouldRemove(Student student) {
    return "Special Ed".equalsIgnoreCase(student.getHonors()) || "Not Walking".equalsIgnoreCase(student.getHonors())
        || "Removal".equalsIgnoreCase(student.getHonors());
  }
}
