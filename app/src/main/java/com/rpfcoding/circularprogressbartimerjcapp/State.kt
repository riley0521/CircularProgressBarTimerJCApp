package com.rpfcoding.circularprogressbartimerjcapp

data class State(
    val model: Model = Model(),
    val isFinished: Boolean = false,
    val isActive: Boolean = false,
    val isTimerRunning: Boolean = false
)