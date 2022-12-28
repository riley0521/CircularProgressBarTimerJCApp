package com.rpfcoding.circularprogressbartimerjcapp

import java.time.DayOfWeek

data class Model(
    val id: Int = 1,
    val minutes: Int,
    val elapsedTimeInMillis: Long,
    val listOfActiveDaysInWeek: List<Int>,
    val title: String = "Sample Title",
) {
    companion object {
        fun listOfWeekdays(): List<Int> {
            return listOf(
                DayOfWeek.MONDAY.value,
                DayOfWeek.TUESDAY.value,
                DayOfWeek.WEDNESDAY.value,
                DayOfWeek.THURSDAY.value,
                DayOfWeek.FRIDAY.value
            )
        }
    }
}
