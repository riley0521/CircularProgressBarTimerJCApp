package com.rpfcoding.circularprogressbartimerjcapp

data class State(
    val model: Model? = null,
    val isFinished: Boolean = false,
    val isActive: Boolean = false,
    val isTimerRunning: Boolean = false
)