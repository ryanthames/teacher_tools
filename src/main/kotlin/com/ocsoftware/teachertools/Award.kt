package com.ocsoftware.teachertools

data class Award(
    val firstName: String,
    val lastName: String,
    val category: String,
    val awardName: String,
    val awardType: AwardType,
    val nhs: Boolean
)