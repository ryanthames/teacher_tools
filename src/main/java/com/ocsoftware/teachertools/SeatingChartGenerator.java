package com.ocsoftware.teachertools;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SeatingChartGenerator {
  private static final String[] rows =
      {"B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U"};

  private static final Map<String, Integer> rowLimits;

  private static int currentSeat = 1;
  private static int currentRowIndex = 0;

  private static final Map<Award, Person> awards = new LinkedHashMap<>();
  private static final Map<Person, Seat> seatingChart = new LinkedHashMap<>();
  private static final Set<Person> people = new LinkedHashSet<>();

  private static final List<String> priorityCategories = new ArrayList<>();

  static {
    rowLimits = new HashMap<>();

    rowLimits.put("B", 9);
    rowLimits.put("C", 9);
    rowLimits.put("D", 10);
    rowLimits.put("E", 10);
    rowLimits.put("F", 10);
    rowLimits.put("G", 11);
    rowLimits.put("H", 11);
    rowLimits.put("I", 11);
    rowLimits.put("J", 12);
    rowLimits.put("K", 12);
    rowLimits.put("L", 13);
    rowLimits.put("M", 13);
    rowLimits.put("N", 13);
    rowLimits.put("O", 14);
    rowLimits.put("P", 14);
    rowLimits.put("Q", 15);
    rowLimits.put("R", 15);
    rowLimits.put("S", 15);
    rowLimits.put("T", 16);
    rowLimits.put("U", 21);
  }

  public static void main(String... args) {
    readDataFromFile();
    markPriorityCategoryStudents();
    initSeatingChart();
    generateSeatingChart();

    try {
      generateOutputBySeat();
      generateOutputAlphabetically();
      generateOutputByOrderCalled();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // TODO: 4/8/18 DEBUGGING - delete
//    seatingChart.entrySet().stream()
//        .sorted(Map.Entry.comparingByValue())
//        .forEach(System.out::println);
  }

  private static void markPriorityCategoryStudents() {
    for(Map.Entry<Award, Person> e : awards.entrySet()) {
      if (isSmallCategoryAward(e.getKey().getAwardName())) {
        Person p = e.getValue();

        for (Person p1 : people) {
          if(p1.equals(p)) {
            p1.setInPriorityCategory(true);
          }
        }
      }
    }
  }

  /**
   * Simpre fidelis
   * Boys and girls state
   * * Outstanding *
   * Principal's Leadership
   * Hargrave award
   * Community "Scholarships"
   * Mr and Mrs CHS
   */
  private static boolean isSmallCategoryAward(String awardName) {
    return awardName.equalsIgnoreCase("Simpre Fidelis")
        || awardName.equalsIgnoreCase("Boys State Representative")
        || awardName.equalsIgnoreCase("Girls State Representative")
        || awardName.contains("Outstanding")
        || awardName.equalsIgnoreCase("Principal's Leadership")
        || awardName.contains("Hargrave")
        || awardName.contains("Scholar")
        || awardName.equalsIgnoreCase("Mr. CHS")
        || awardName.equalsIgnoreCase("Miss CHS");
  }

  private static void populatePriorityCategories() {
    Map<String, Integer> categoryCount = new HashMap<>();

    for(Map.Entry<Award, Person> e : awards.entrySet()) {
      String category = e.getKey().getCategory();
      categoryCount.merge(category, 1, Integer::sum);
    }

    categoryCount.entrySet()
        .stream()
        .filter(e -> e.getValue() >= 1 && e.getValue() < 7)
        .map(e -> e.getKey())
        .forEach(priorityCategories::add);
  }

  private static void generateOutputByOrderCalled() throws IOException {
    try(BufferedWriter writer = new BufferedWriter(new FileWriter("SeatingChartOrderCalled.csv"))) {
      for(Map.Entry<Award, Person> e : awards.entrySet()) {
        Seat s = seatingChart.get(e.getValue());
        writer.write(String.format("%s,%s,%s,%s\n", e.getValue().getFirstName(), e.getValue().getLastName(),
            e.getKey().getCategory(), s));
      }
    }
  }

  private static void generateOutputAlphabetically() throws IOException {
    try(BufferedWriter writer = new BufferedWriter(new FileWriter("SeatingChartAlphabetically.csv"))) {
      List<Person> sortedPeeps = people.stream().sorted(Comparator.comparing(Person::getLastName)).collect(Collectors.toList());

      for(Person p : sortedPeeps) {
        writer.write(String.format("%s,%s,%s\n", p.getFirstName(), p.getLastName(), seatingChart.get(p)));
      }
    }
  }

  private static void generateOutputBySeat() throws IOException {
    Map<Person, Seat> chartOrderedBySeat = new LinkedHashMap<>();
    seatingChart.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .forEachOrdered(x -> chartOrderedBySeat.put(x.getKey(), x.getValue()));

    try (BufferedWriter writer = new BufferedWriter(new FileWriter("SeatingChartOrderBySeat.csv"))) {
      chartOrderedBySeat.forEach((p, s) -> {
        List<Award> studentAwards = new LinkedList<>();
        awards.entrySet().stream().filter(e -> p.equals(e.getValue())).forEach(e -> studentAwards.add(e.getKey()));

        for (Award sa : studentAwards) {
          try {
            writer.write(String.format("%s,%s,%s,%s\n", p.getFirstName(), p.getLastName(), sa.getCategory(), s));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    }
  }

  private static void generateSeatingChart() {
    List<Person> ssStudents = people.stream().filter(Person::isSs).collect(Collectors.toList());
    List<Person> nhsStudents = people.stream().filter(Person::isNhs).collect(Collectors.toList());

    // filter out special cases (blanks, teachers, etc)
    List<Person> specialCases = people.stream()
        .filter(SeatingChartGenerator::isSpecialCase)
        .collect(Collectors.toList());

    List<Person> remainingStudents = people.stream().filter(p -> !p.isSs() && !p.isNhs() && !isSpecialCase(p))
        .collect(Collectors.toList());

    Map<Person, Integer> awardCount = generateAwardCountMap();

    List<Person> multipleAwardWinners = remainingStudents.stream().filter(p -> awardCount.get(p) > 1)
        .collect(Collectors.toList());
    List<Person> singleAwardWinners = remainingStudents.stream().filter(p -> awardCount.get(p) == 1)
        .collect(Collectors.toList());

    List<Person> priorityCategoryWinners = singleAwardWinners.stream().filter(Person::isInPriorityCategory).collect(Collectors.toList());
    List<Person> rest = singleAwardWinners.stream().filter(p -> !p.isInPriorityCategory()).collect(Collectors.toList());

    // assign ss and nhs students
    ssStudents.forEach(s -> seatingChart.put(s, new Seat("Row A", 0)));
    nhsStudents.forEach(s -> seatingChart.put(s, new Seat("Stage", 0)));
    specialCases.forEach(s -> seatingChart.put(s, new Seat("Stage", 0)));

    multipleAwardWinners.sort(Comparator.comparing(Person::getLastName));

    multipleAwardWinners.forEach(SeatingChartGenerator::assignSeat);
    priorityCategoryWinners.forEach(SeatingChartGenerator::assignSeat);
    rest.forEach(SeatingChartGenerator::assignSeat);
  }

  // method for handling one-off weirdness (aka blank names, teachers, etc)
  private static boolean isSpecialCase(Person p) {
    return (StringUtils.isBlank(p.getLastName()) || StringUtils.isBlank(p.getFirstName()))
        || "None".equals(p.getFirstName());
  }

  private static void assignSeat(Person s) {
    validateIndex();
    seatingChart.put(s, new Seat(rows[currentRowIndex], currentSeat));
    currentSeat++;
  }

  private static void validateIndex() {
    if (currentSeat > rowLimits.get(rows[currentRowIndex])) {
      resetIndices();
    }
  }

  private static void resetIndices() {
    currentRowIndex++;
    currentSeat = 1;
  }

  private static void initSeatingChart() {
    people.forEach(p -> seatingChart.put(p, new Seat("Unassigned", 0)));
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

    person.setFirstName(tokens[0].trim());
    person.setLastName(tokens[1].trim());
    award.setCategory(tokens[2].trim());
    award.setAwardName(tokens[3].trim());
    award.setAwardType(awardType);
    boolean ss = false;
    boolean nhs = false;

    if (tokens.length > 4) {
      nhs = StringUtils.isNotBlank(tokens[4]) ? Boolean.valueOf(tokens[4].trim()) : false;
    }

    if (tokens.length > 5) {
      ss = StringUtils.isNotBlank(tokens[5]) ? Boolean.valueOf(tokens[5].trim().toLowerCase()) : false;
    }

    Person existingPerson = getPerson(person);

    if (existingPerson != null) {
      person = existingPerson;
    } else {
      people.add(person);
    }

    if (ss) {
      person.setSs(true);
    }

    if(nhs) {
      person.setNhs(true);
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
