package com.ocsoftware.teachertools;

public class Award {
  private String category;
  private String awardName;
  private AwardType awardType;

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

  @Override
  public String toString() {
    return String.format("%s - %s", awardName, category);
  }
}
