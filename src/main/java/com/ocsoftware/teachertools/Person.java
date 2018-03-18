package com.ocsoftware.teachertools;

public class Person {
  private String firstName;
  private String lastName;
  private boolean nhs;
  private boolean ss;

  public boolean isSs() {
    return ss;
  }

  public void setSs(boolean ss) {
    this.ss = ss;
  }

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

  public boolean isNhs() {
    return nhs;
  }

  public void setNhs(boolean nhs) {
    this.nhs = nhs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Person)) return false;

    Person person = (Person) o;

    return firstName.equals(person.firstName) && lastName.equals(person.lastName);
  }

  @Override
  public int hashCode() {
    int result = firstName.hashCode();
    result = 31 * result + lastName.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return String.format("%s %s", firstName, lastName);
  }
}
