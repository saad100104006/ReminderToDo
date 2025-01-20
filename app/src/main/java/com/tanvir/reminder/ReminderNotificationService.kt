package com.tanvir.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tanvir.reminder.domain.model.Reminder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReminderNotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleNotification(reminder: Reminder) {
        val alarmIntent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            putExtra(REMINDER_INTENT, reminder)
        }

        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmService = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = reminder.time.time

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                alarmService.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    alarmPendingIntent
                )
            } catch (exception: SecurityException) {
                FirebaseCrashlytics.getInstance().recordException(exception)
            }
        }
    }

    companion object {
        const val REMINDER_CHANNEL_ID = "reminder_channel"
    }
}
