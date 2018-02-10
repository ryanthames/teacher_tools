package com.ocsoftware.teachertools

import java.util.*
import kotlin.collections.LinkedHashMap

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
    awardCount[name] = awardCount.getOrPut(name, { 1 }) + 1
  }
  return awardCount
}