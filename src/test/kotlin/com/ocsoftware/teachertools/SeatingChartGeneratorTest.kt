package com.ocsoftware.teachertools

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class SeatingChartGeneratorTest : StringSpec() {
  init {
    generateInput()

    "multiple award winners should be seated first" {
      val orderBySeat = com.ocsoftware.teachertools.generateSeatingChart(ChartType.SEAT, ArrayList())
      val awards = orderBySeat.map { it.key }

      awards[0].lastName shouldBe "Kirk"
      awards[1].lastName shouldBe "Sulu"
      awards[2].lastName shouldBe "Thames"
    }.config(enabled = false)

    "only special services should be assigned to row A" {
      // todo
    }.config(enabled = false)

    "all NHS officers should be assigned to the stage" {
      // todo
    }.config(enabled = false)
  }

  fun generateInput() {
    // TODO generate input
  }
}