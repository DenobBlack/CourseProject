package com.example.fitnessapp.data.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import java.time.LocalDate
import java.time.ZoneId

fun createNotificationChannel(context: Context) {
    val name = "Workout reminders"
    val descriptionText = "Вы сегодня запланировали тренировку \uD83D\uDC40"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel("workout_channel", name, importance).apply {
        description = descriptionText
    }
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

fun scheduleWorkoutNotification(context: Context, date: LocalDate, workoutName: String, notificationId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, WorkoutReminderReceiver::class.java).apply {
        putExtra("WORKOUT_NAME", workoutName)
        putExtra("NOTIFICATION_ID", notificationId)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val triggerDateTime = date.atTime(12, 0)

    val triggerMillis = triggerDateTime
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    if (triggerMillis < System.currentTimeMillis()) return


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(
                context,
                "Разрешите точные будильники в настройках для уведомлений о тренировках",
                Toast.LENGTH_LONG
            ).show()

            val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(settingsIntent)
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            return
        }
    }
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
}

fun cancelWorkoutNotification(context: Context, notificationId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, WorkoutReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}