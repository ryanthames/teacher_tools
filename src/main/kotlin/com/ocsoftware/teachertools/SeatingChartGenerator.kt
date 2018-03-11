package com.ocsoftware.teachertools

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.*
import kotlin.collections.LinkedHashMap

fun main(args: Array<String>) {
  val awards = ArrayList<Award>()

  readCsvFile(awards, "Academic.csv", AwardType.ACADEMIC)
  readCsvFile(awards, "School.Community.csv", AwardType.SCHOOL_COMMUNITY)

  val seartingChart = generateSeatingChart(ChartType.SEAT, awards)

  println("DONE")
}

private fun readCsvFile(awards: ArrayList<Award>, fileName: String, awardType: AwardType) {
  var fileReader: BufferedReader? = null

  try {
    var line: String?

    fileReader = BufferedReader(FileReader(fileName))

    // ignore header
    fileReader.readLine()

    line = fileReader.readLine()
    while (line != null) {
      val tokens = line.split(",")
      if (tokens.isNotEmpty()) {
        val award = Award(
            tokens[0],
            tokens[1],
            tokens[2],
            tokens[3],
            awardType,
            tokens[4].toBoolean()
        )

        awards.add(award)
      }

      line = fileReader.readLine()
    }
  } catch (e: Exception) {
    println("Reading CSV Error!")
    e.printStackTrace()
  } finally {
    try {
      fileReader!!.close()
    } catch (e: IOException) {
      println("Closing fileReader error")
      e.printStackTrace()
    }
  }
}

// TODO Generate based on chart type
// TODO REFACTOR!!
fun generateSeatingChart(type: ChartType, awards: ArrayList<Award>): Map<Award, String> {
  val nhsOfficerAwards = awards.filter { it.nhs }
  val ssAwards = awards.filter { it.category == "Special Services" }
  val remainingAwards = awards.filter { !it.nhs && it.category != "Special Services" }

  val awardCount = generateAwardCount(awards)

  val multipleAwardNames = awardCount.filter { it.value > 1 }
  val singleAwardNames = awardCount.filter { it.value == 1 }

  val multipleAwards = remainingAwards.filter {
    val name = "${it.firstName} ${it.lastName}"
    multipleAwardNames.getOrDefault(name, 0) > 1
  }.sortedWith(compareBy({ it.awardType }, { it.lastName }))

  val singleAwards = remainingAwards.filter {
    val name = "${it.firstName} ${it.lastName}"
    singleAwardNames.getOrDefault(name, 0) == 1
  }.sortedWith(compareBy({ it.awardType }, { it.lastName }))

  val seatingChart = LinkedHashMap<Award, String>()

  nhsOfficerAwards.forEach({
    seatingChart[it] = "Stage"
  })

  var seat = 1
  ssAwards.forEach({
    seatingChart[it] = "A$seat"
    seat += 1
  })

  seat = 1
  var row = 'B'
  multipleAwards.forEach({
    if(seat > 15) {
      seat = 1
      row = row.inc()
    }
    seatingChart[it] = "$row$seat"
    seat += 1
  })

  singleAwards.forEach({
    if(seat > 15) {
      seat = 1
      row = row.inc()
    }
    seatingChart[it] = "$row$seat"
    seat += 1
  })

  return seatingChart
}

private fun generateAwardCount(awards: ArrayList<Award>): HashMap<String, Int> {
  val awardCount = HashMap<String, Int>()

  awards.forEach {
    val name = "${it.firstName} ${it.lastName}"
    val ac: Int? = awardCount[name]

    if(ac == null) {
      awardCount[name] = 1
    } else {
      awardCount[name] = ac + 1
    }
  }

  return awardCount
}