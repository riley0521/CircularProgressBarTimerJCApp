package com.rpfcoding.circularprogressbartimerjcapp

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

class TimerService : LifecycleService() {

    private var firstRun: Boolean = false

    private var totalTime: Long = 0L
    private var serviceScope: Job? = null

    companion object {
        private val mElapsedTime = MutableStateFlow(0L)
        val elapsedTime = mElapsedTime.asStateFlow()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            TimerServiceActions.ACTION_START -> start(
                intent.getLongExtra("TOTAL_TIME", 0L),
                intent.getLongExtra("ELAPSED_TIME", 0L),
            )
            TimerServiceActions.ACTION_STOP -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun stop() {
        serviceScope?.cancel()
        serviceScope = null

        Log.d("TimerService.kt", "I'm stopping!")

        stopForeground(true)
        stopSelf()
    }

    private fun start(totalTime: Long, elapsedTime: Long) {
        this.totalTime = totalTime
        if (elapsedTime > 0L && !firstRun) {
            firstRun = true
            mElapsedTime.update { elapsedTime }
        }

        val notification = NotificationCompat.Builder(this, "circular_progress_bar_timer_id")
            .setContentTitle("Timer")
            .setContentText("00:00")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)

        startForeground(74, notification.build())

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        serviceScope?.cancel()
        serviceScope = lifecycleScope.launch {
            while (true) {
                delay(1000L)
                if(totalTime != mElapsedTime.value) {
                    mElapsedTime.update { it + 1000L }
                    notification.setContentText(getFormattedTime(totalTime - mElapsedTime.value))
                    notificationManager.notify(74, notification.build())
                } else {
                    stop()
                    return@launch
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope?.cancel()
    }

    object TimerServiceActions {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}