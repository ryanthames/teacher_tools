package com.ocsoftware.teachertools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SeatingChartGenerator {
  public static void main(String... args) {
    List<Award> awards = new ArrayList<>();

    try {
      readCsvFile(awards, "Academic.csv", AwardType.ACADEMIC);
      readCsvFile(awards, "School.Community.csv", AwardType.SCHOOL_COMMUNITY);
    } catch (IOException e) {
      System.out.println("Error reading CSV files:\n");
      e.printStackTrace();
    }

    Map<Award, String> seatingChart = generateSeatingChart(ChartType.SEAT, awards);
  }

  private static void readCsvFile(List<Award> awards, String fileName, AwardType awardType) throws IOException {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
      // ignore header
      bufferedReader.readLine();

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        String[] tokens = line.split(",");
        if (tokens.length != 0) {
          addAward(awards, awardType, tokens);
        }
      }
    }
  }

  private static void addAward(List<Award> awards, AwardType awardType, String[] tokens) {
    Award award = new Award();

    award.setFirstName(tokens[0]);
    award.setLastName(tokens[1]);
    award.setCategory(tokens[2]);
    award.setAwardName(tokens[3]);
    award.setAwardType(awardType);
    award.setNhs(Boolean.valueOf(tokens[4]));

    awards.add(award);
  }

  private static Map<Award, String> generateSeatingChart(ChartType chartType, List<Award> awards) {
    Map<Award, String> seatingChart = new LinkedHashMap<>();

    List<Award> nhsOfficerAwards = awards.stream().filter(Award::isNhs).collect(Collectors.toList());
    List<Award> ssAwards = awards.stream().filter(a -> "Special Services".equals(a.getCategory())).collect(Collectors.toList());

    // TODO: 3/17/18 BAD BAD BAD...refactor
    List<Award> remainingAwards = awards.stream().filter(a -> !"Special Services".equals(a.getCategory()) && !a.isNhs()).collect(Collectors.toList());

    Map<String, Integer> awardCount = generateAwardCountMap(awards);

    List<Award> multipleAwardWinners = remainingAwards.stream().filter(a -> {
      String name = String.format("%s %s", a.getFirstName(), a.getLastName());
      return awardCount.getOrDefault(name, 0) > 1;
    }).collect(Collectors.toList());

    List<Award> singleAwardWinners = remainingAwards.stream().filter(a -> {
      String name = String.format("%s %s", a.getFirstName(), a.getLastName());
      return awardCount.getOrDefault(name, 0) == 1;
    }).collect(Collectors.toList());

    // TODO: 3/17/18 sort

    // TODO: 3/17/18 assuming 15 seats per row for now....need to get the real numbers

    return seatingChart;
  }

  private static Map<String, Integer> generateAwardCountMap(List<Award> awards) {
    Map<String, Integer> awardCount = new HashMap<>();

    awards.forEach(a -> {
      String name = String.format("%s %s", a.getFirstName(), a.getLastName());
      Integer count = awardCount.get(name);

      if(count == null) {
        awardCount.put(name, 1);
      } else {
        awardCount.put(name, ++count);
      }
    });

    return awardCount;
  }
}
