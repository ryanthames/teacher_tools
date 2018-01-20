package com.ocsoftware.teachertools

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec

class SeatingChartGeneratorTest : StringSpec() {
  init {
    generateInput()

    "multiple award winners should be seated first" {
      // TODO implement test
      println("Hello")
      1 shouldEqual 2
    }

    "only special services should be assigned to row A" {
      // todo
    }.config(enabled = false)

    "all NHS officers should be assigned to the stage" {
      // todo
    }.config(enabled = false)
  }

  fun generateInput() {
    // TODO
  }
}