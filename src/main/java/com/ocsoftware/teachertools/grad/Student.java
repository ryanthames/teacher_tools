package com.ocsoftware.teachertools.grad;

import java.util.Objects;

public class Student {
  private String firstMiddleName;
  private String lastName;
  private String honors;

  public Student(String firstMiddleName, String lastName, String honors) {
    this.firstMiddleName = firstMiddleName;
    this.lastName = lastName;
    this.honors = honors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Student)) return false;
    Student student = (Student) o;
    return Objects.equals(getFirstMiddleName(), student.getFirstMiddleName()) &&
        Objects.equals(getLastName(), student.getLastName()) &&
        Objects.equals(getHonors(), student.getHonors());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFirstMiddleName(), getLastName(), getHonors());
  }

  @Override
  public String toString() {
    return "Student{" +
        "firstMiddleName='" + firstMiddleName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", honors='" + honors + '\'' +
        '}';
  }

  public String getFirstMiddleName() {
    return firstMiddleName;
  }

  public void setFirstMiddleName(String firstMiddleName) {
    this.firstMiddleName = firstMiddleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getHonors() {
    return honors;
  }

  public void setHonors(String honors) {
    this.honors = honors;
  }
}
