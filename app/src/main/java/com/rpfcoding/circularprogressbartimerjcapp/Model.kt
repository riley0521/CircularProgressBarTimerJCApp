package com.rpfcoding.circularprogressbartimerjcapp

import java.time.DayOfWeek

data class Model(
    val id: Int = 1,
    val minutes: Int = 2,
    val elapsedTimeInMillis: Long = 110_000L,
    val listOfActiveDaysInWeek: List<Int> = listOf(
        DayOfWeek.MONDAY.value,
        DayOfWeek.TUESDAY.value,
        DayOfWeek.WEDNESDAY.value,
        DayOfWeek.THURSDAY.value,
        DayOfWeek.FRIDAY.value
    )
)
