package com.ocsoftware.teachertools;

public class Award {
  private String firstName;
  private String lastName;
  private String category;
  private String awardName;
  private AwardType awardType;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getAwardName() {
    return awardName;
  }

  public void setAwardName(String awardName) {
    this.awardName = awardName;
  }

  public AwardType getAwardType() {
    return awardType;
  }

  public void setAwardType(AwardType awardType) {
    this.awardType = awardType;
  }

  public boolean isNhs() {
    return nhs;
  }

  public void setNhs(boolean nhs) {
    this.nhs = nhs;
  }

  private boolean nhs;
}
