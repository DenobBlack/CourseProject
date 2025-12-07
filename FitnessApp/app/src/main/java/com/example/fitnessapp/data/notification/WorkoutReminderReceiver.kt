package com.example.fitnessapp.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fitnessapp.R

class WorkoutReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val workoutName = intent.getStringExtra("WORKOUT_NAME") ?: "Тренировка"
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)

        val builder = NotificationCompat.Builder(context, "workout_channel")
            .setSmallIcon(R.drawable.ic_fire)
            .setContentTitle("Запланированная тренировка")
            .setContentText("Сегодня: $workoutName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}