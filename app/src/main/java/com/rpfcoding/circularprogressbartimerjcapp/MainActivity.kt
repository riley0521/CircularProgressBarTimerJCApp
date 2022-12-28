package com.rpfcoding.circularprogressbartimerjcapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rpfcoding.circularprogressbartimerjcapp.ui.theme.CircularProgressBarTimerJCAppTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        setContent {
            CircularProgressBarTimerJCAppTheme {
                val viewModel: MainViewModel = viewModel()

                Surface(
                    color = Color(0xFF101010), modifier = Modifier.fillMaxSize()
                ) {
                    val state by viewModel.state.collectAsState()

                    val model by derivedStateOf {
                        state.model
                    }

                    val totalTime by derivedStateOf {
                        state.model.minutes * 60L * 1000L
                    }

                    LaunchedEffect(true) {
                        TimerService.elapsedTime.collectLatest {
                            if (!state.isFinished && state.isTimerRunning) {
                                viewModel.updateElapsedTime(it)
                            }
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        TimerCircle(
                            remainingTime = totalTime - model.elapsedTimeInMillis,
                            percentage = (totalTime - model.elapsedTimeInMillis) / totalTime.toFloat(),
                            handleColor = Color.Green,
                            inactiveBarColor = Color.DarkGray,
                            activeBarColor = Color(0xFF37B900),
                            isActive = state.isActive,
                            isTimerRunning = state.isTimerRunning,
                            modifier = Modifier.size(250.dp),
                            onToggleClick = { isTimerRunning ->
                                viewModel.toggle(isTimerRunning)

                                if (isTimerRunning) {
                                    Intent(this@MainActivity, TimerService::class.java).apply {
                                        action = TimerService.TimerServiceActions.ACTION_START
                                        putExtra("TOTAL_TIME", totalTime)
                                        putExtra("ELAPSED_TIME", model.elapsedTimeInMillis)
                                        startService(this)
                                    }
                                } else {
                                    Intent(this@MainActivity, TimerService::class.java).apply {
                                        action = TimerService.TimerServiceActions.ACTION_STOP
                                        startService(this)
                                    }
                                }
                            })
                    }
                }
            }
        }
    }
}