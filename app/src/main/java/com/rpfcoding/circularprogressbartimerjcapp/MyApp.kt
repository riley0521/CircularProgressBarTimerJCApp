package com.rpfcoding.circularprogressbartimerjcapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "circular_progress_bar_timer_id",
                "Timer",
                NotificationManager.IMPORTANCE_LOW
            )

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}