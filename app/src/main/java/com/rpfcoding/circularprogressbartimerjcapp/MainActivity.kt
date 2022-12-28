package com.rpfcoding.circularprogressbartimerjcapp

import android.Manifest
import android.content.Intent
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rpfcoding.circularprogressbartimerjcapp.ui.theme.CircularProgressBarTimerJCAppTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!applicationContext.hasPostNotificationPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    55
                )
            }
        }

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

                    if (model == null) {
                        return@Surface
                    }

                    LaunchedEffect(true) {
                        launch {
                            TimerService.elapsedTime.collectLatest {

                                if (!state.isFinished && state.isTimerRunning) {
                                    viewModel.updateElapsedTime(it)
                                }
                            }
                        }
                    }

                    LaunchedEffect(key1 = model?.minutes) {
                        if (!state.isFinished && state.isTimerRunning) {
                            startTimerService(model)
                        }
                    }

                    val remainingTime by derivedStateOf {
                        model?.minutes?.convertMinutesToMillis()
                            ?.minus(model?.elapsedTimeInMillis ?: 0L) ?: 0L
                    }

                    val percentage by derivedStateOf {
                        remainingTime.div(model?.minutes?.convertMinutesToMillis()?.toFloat() ?: 0f)
                    }

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        TimerCircle(
                            remainingTime = remainingTime,
                            percentage = percentage,
                            handleColor = Color.Green,
                            inactiveBarColor = Color.DarkGray,
                            activeBarColor = Color(0xFF37B900),
                            isActive = state.isActive,
                            isTimerRunning = state.isTimerRunning,
                            modifier = Modifier.size(250.dp),
                            onToggleClick = { isTimerRunning ->
                                viewModel.toggle(isTimerRunning)

                                if (isTimerRunning) {
                                    startTimerService(model)
                                } else {
                                    Intent(this@MainActivity, TimerService::class.java).apply {
                                        action = TimerService.TimerServiceActions.ACTION_STOP
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            startForegroundService(this)
                                        } else {
                                            startService(this)
                                        }
                                    }
                                }
                            })
                    }
                }
            }
        }
    }

    private fun startTimerService(model: Model?) {
        Intent(this@MainActivity, TimerService::class.java).apply {
            action = TimerService.TimerServiceActions.ACTION_START
            putExtra("TOTAL_TIME", model?.minutes?.convertMinutesToMillis())
            putExtra("ELAPSED_TIME", model?.elapsedTimeInMillis)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(this)
            } else {
                startService(this)
            }
        }
    }
}