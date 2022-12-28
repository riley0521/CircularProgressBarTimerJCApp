package com.rpfcoding.circularprogressbartimerjcapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rpfcoding.circularprogressbartimerjcapp.ui.theme.CircularProgressBarTimerJCAppTheme
import java.time.DayOfWeek
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CircularProgressBarTimerJCAppTheme {
                Surface(
                    color = Color(0xFF101010),
                    modifier = Modifier.fillMaxSize()
                ) {
                    val totalTime by remember {
                        // 2 is int and it comes from the database and convert it to minutes
                        mutableStateOf(2 * 60L * 1000L)
                    }

                    var elapsedTime by remember {
                        mutableStateOf(115_000L)
                    }

                    val isFinished by remember(totalTime, elapsedTime) {
                        derivedStateOf {
                            totalTime == elapsedTime
                        }
                    }

                    val daysFromDatabase by remember {
                        mutableStateOf(
                            listOf(
                                DayOfWeek.MONDAY,
                                DayOfWeek.TUESDAY,
                                DayOfWeek.WEDNESDAY,
                                DayOfWeek.THURSDAY,
                                DayOfWeek.FRIDAY,
                            )
                        )
                    }

                    val isActive by remember(totalTime, elapsedTime, daysFromDatabase) {
                        // Check if totalTime is not equals to elapsedTime to know
                        // if the user already done this task for this day.
                        // And check if this task is applicable for this day
                        // Let's say Exercising is only applicable for Mon, Wed, and Thurs and today is Tuesday
                        // So this should be false.
                        // totalTime == elapsedTime && dayOfWeek == today

                        derivedStateOf {
                            !isFinished && daysFromDatabase.any { it == LocalDate.now().dayOfWeek }
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        TimerCircle(
                            totalTime = totalTime,
                            elapsedTime = elapsedTime,
                            handleColor = Color.Green,
                            inactiveBarColor = Color.DarkGray,
                            activeBarColor = Color(0xFF37B900),
                            isActive = isActive,
                            modifier = Modifier.size(250.dp),
                            onTimerTick = {
                                // Every time we hit pause, we cannot save the elapsed time millis
                                // to database because we might affect the performance if
                                // we save it to database every 100 milliseconds

                                elapsedTime = it
                            },
                            onTimerStop = {

                                /**
                                 * Call viewModel to decide whether to save isCompleted or just
                                 * save the elapsedTime.
                                 */
                            }
                        )
                    }
                }
            }
        }
    }
}