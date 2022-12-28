package com.rpfcoding.circularprogressbartimerjcapp

import java.util.concurrent.TimeUnit

fun getFormattedTime(millis: Long): String {
    var milliSeconds = millis

    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
    milliSeconds -= TimeUnit.MINUTES.toMillis(minutes)

    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds)

    return "${if (minutes < 10) "0$minutes" else minutes}:" +
            "${if (seconds < 10) "0$seconds" else seconds}"
}