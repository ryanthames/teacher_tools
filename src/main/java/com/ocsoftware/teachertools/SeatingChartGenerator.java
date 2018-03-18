package com.ocsoftware.teachertools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SeatingChartGenerator {
  private static final Map<Award, Person> awards = new LinkedHashMap<>();
  private static final Map<Person, String> seatingChart = new LinkedHashMap<>();
  private static final Set<Person> people = new LinkedHashSet<>();

  public static void main(String... args) {
    readDataFromFile();
    initSeatingChart();
    generateSeatingChart();
    // TODO: 3/17/18 generating output based on criteria
  }

  private static void generateSeatingChart() {
    // TODO: 3/18/18 get NHS students
    // TODO: 3/18/18 get Special Services students
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

    if(existingPerson != null) {
      person = existingPerson;
    } else {
      people.add(person);
    }

    if("Special Services".equals(award.getCategory())) {
      person.setSs(true);
    }

    awards.put(award, person);
  }

  private static Person getPerson(Person person) {
    List<Person> result = people.stream().filter(p -> p.equals(person)).collect(Collectors.toList());

    if(result != null && result.size() > 0) {
      // Should only be one result
      return result.get(0);
    } else {
      return null;
    }
  }

//  private static Map<Award, String> generateSeatingChart(ChartOrder chartType, List<Award> awards) {
//    Map<Award, String> seatingChart = new LinkedHashMap<>();
//
//    List<Award> nhsOfficerAwards = awards.stream().filter(Award::isNhs).collect(Collectors.toList());
//    List<Award> ssAwards = awards.stream().filter(a -> "Special Services".equals(a.getCategory())).collect(Collectors.toList());
//
//    // TODO: 3/17/18 BAD BAD BAD...refactor
//    List<Award> remainingAwards = awards.stream().filter(a -> !"Special Services".equals(a.getCategory()) && !a.isNhs()).collect(Collectors.toList());
//
//    Map<String, Integer> awardCount = generateAwardCountMap(awards);
//
//    List<Award> multipleAwardWinners = remainingAwards.stream().filter(a -> {
//      String name = String.format("%s %s", a.getFirstName(), a.getLastName());
//      return awardCount.getOrDefault(name, 0) > 1;
//    }).collect(Collectors.toList());
//
//    List<Award> singleAwardWinners = remainingAwards.stream().filter(a -> {
//      String name = String.format("%s %s", a.getFirstName(), a.getLastName());
//      return awardCount.getOrDefault(name, 0) == 1;
//    }).collect(Collectors.toList());
//
//    // TODO: 3/17/18 sort
//
//    // TODO: 3/17/18 assuming 15 seats per row for now....need to get the real numbers
//
//    return seatingChart;
//  }
//
//  private static Map<String, Integer> generateAwardCountMap(List<Award> awards) {
//    Map<String, Integer> awardCount = new HashMap<>();
//
//    awards.forEach(a -> {
//      String name = String.format("%s %s", a.getFirstName(), a.getLastName());
//      Integer count = awardCount.get(name);
//
//      if(count == null) {
//        awardCount.put(name, 1);
//      } else {
//        awardCount.put(name, ++count);
//      }
//    });
//
//    return awardCount;
//  }
}
