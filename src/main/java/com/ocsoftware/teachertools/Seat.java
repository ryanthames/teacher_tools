package com.ocsoftware.teachertools;

import java.util.Comparator;

public class Seat implements Comparable<Seat> {
  private String row;
  private Integer seat;

  public Seat(String row, Integer seat) {
    this.row = row;
    this.seat = seat;
  }

  public String getRow() {
    return row;
  }

  public void setRow(String row) {
    this.row = row;
  }

  public Integer getSeat() {
    return seat;
  }

  public void setSeat(Integer seat) {
    this.seat = seat;
  }

  @Override
  public String toString() {
    return seat != 0 ? String.format("%s%s", row, seat) : row;
  }

  @Override
  public int compareTo(Seat o) {
    return Comparator.comparing(Seat::getRow)
                  .thenComparing(Seat::getSeat)
                  .compare(this, o);
  }
}
