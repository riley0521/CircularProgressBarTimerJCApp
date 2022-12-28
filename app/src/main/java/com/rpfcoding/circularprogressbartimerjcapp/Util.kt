package com.rpfcoding.circularprogressbartimerjcapp

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit

fun getFormattedTime(millis: Long): String {
    var milliSeconds = millis

    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
    milliSeconds -= TimeUnit.MINUTES.toMillis(minutes)

    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds)

    return "${if (minutes < 10) "0$minutes" else minutes}:" +
            "${if (seconds < 10) "0$seconds" else seconds}"
}

fun Int.convertMinutesToMillis(): Long {
    return TimeUnit.MINUTES.toMillis(this.toLong())
}

fun Context.hasPostNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else true
}

fun Context.showNotification(
    title: String,
    desc: String
) {
    val notification = NotificationCompat.Builder(this, "circular_progress_bar_timer_id")
        .setContentTitle(title)
        .setContentText(desc)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .build()

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(75, notification)
}