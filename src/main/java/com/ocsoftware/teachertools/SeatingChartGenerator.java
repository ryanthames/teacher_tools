package com.ocsoftware.teachertools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SeatingChartGenerator {
  private static final Integer[] seats = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
  private static final String[] rows = {"B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P"};

  private static int currentSeatIndex = 0;
  private static int currentRowIndex = 0;

  private static final Map<Award, Person> awards = new LinkedHashMap<>();
  private static final Map<Person, String> seatingChart = new LinkedHashMap<>();
  private static final Set<Person> people = new LinkedHashSet<>();

  public static void main(String... args) {
    readDataFromFile();
    initSeatingChart();
    generateSeatingChart();
    // TODO: 3/17/18 generating output based on criteria

    seatingChart.forEach((p, s) -> System.out.println(String.format("%s | %s", p.toString(), s)));
  }

  private static void generateSeatingChart() {
    List<Person> ssStudents = people.stream().filter(Person::isSs).collect(Collectors.toList());
    List<Person> nhsStudents = people.stream().filter(Person::isNhs).collect(Collectors.toList());
    List<Person> remainingStudents = people.stream().filter(p -> !p.isSs() && !p.isNhs()).collect(Collectors.toList());

    Map<Person, Integer> awardCount = generateAwardCountMap();

    List<Person> multipleAwardWinners = remainingStudents.stream().filter(p -> awardCount.get(p) > 1).collect(Collectors.toList());
    List<Person> singleAwardWinners = remainingStudents.stream().filter(p -> awardCount.get(p) == 1).collect(Collectors.toList());

    // assign ss and nhs students
    ssStudents.forEach(s -> seatingChart.put(s, "Row A"));
    nhsStudents.forEach(s -> seatingChart.put(s, "Stage"));

    multipleAwardWinners.forEach(SeatingChartGenerator::assignSeat);
    singleAwardWinners.forEach(SeatingChartGenerator::assignSeat);

    // TODO: 3/18/18 handle teachers and blanks
    // TODO: 3/18/18 handle sorting between different buckets of students
    // TODO: 3/18/18 handle missing students
  }

  private static void assignSeat(Person s) {
    validateIndex();
    seatingChart.put(s, String.format("%s%d", rows[currentRowIndex], seats[currentSeatIndex]));
    currentSeatIndex++;
  }

  private static void validateIndex() {
    if (currentSeatIndex >= seats.length) {
      resetIndices();
    }
  }

  private static void resetIndices() {
    currentRowIndex++;
    currentSeatIndex = 0;
  }

  private static void initSeatingChart() {
    people.forEach(p -> seatingChart.put(p, "Unassigned"));
  }

  private static void readDataFromFile() {
    try {
      readCsvFile("Academic.csv", AwardType.ACADEMIC);
      readCsvFile("School.Community.csv", AwardType.SCHOOL_COMMUNITY);
    } catch (IOException e) {
      System.out.println("Error reading CSV files:\n");
      e.printStackTrace();
    }
  }

  private static void readCsvFile(String fileName, AwardType awardType) throws IOException {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
      // ignore header
      bufferedReader.readLine();

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        String[] tokens = line.split(",");
        if (tokens.length != 0) {
          addAward(awardType, tokens);
        }
      }
    }
  }

  private static void addAward(AwardType awardType, String[] tokens) {
    Award award = new Award();
    Person person = new Person();

    person.setFirstName(tokens[0]);
    person.setLastName(tokens[1]);
    award.setCategory(tokens[2]);
    award.setAwardName(tokens[3]);
    award.setAwardType(awardType);
    person.setNhs(Boolean.valueOf(tokens[4]));

    Person existingPerson = getPerson(person);

    if (existingPerson != null) {
      person = existingPerson;
    } else {
      people.add(person);
    }

    if ("Special Services".equals(award.getCategory())) {
      person.setSs(true);
    }

    awards.put(award, person);
  }

  private static Person getPerson(Person person) {
    List<Person> result = people.stream().filter(p -> p.equals(person)).collect(Collectors.toList());

    if (result != null && result.size() > 0) {
      // Should only be one result
      return result.get(0);
    } else {
      return null;
    }
  }

  private static Map<Person, Integer> generateAwardCountMap() {
    Map<Person, Integer> awardCount = new HashMap<>();

    awards.forEach((a, p) -> {
      Integer count = awardCount.get(p);

      if (count == null) {
        awardCount.put(p, 1);
      } else {
        awardCount.put(p, ++count);
      }
    });

    return awardCount;
  }
}
