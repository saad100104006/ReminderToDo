package com.tanvir.reminder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.tanvir.reminder.domain.model.Reminder
import dagger.hilt.android.AndroidEntryPoint

const val REMINDER_INTENT = "reminder_intent"
const val REMINDER_NOTIFICATION = "reminder_notification"

@AndroidEntryPoint
class ReminderNotificationReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            intent?.getParcelableExtra<Reminder>(REMINDER_INTENT)?.let { reminder ->
                showNotification(it, reminder)
            }
        }
    }

    private fun showNotification(context: Context, reminder: Reminder) {
        // Create deep link intent for notification click
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            data = Uri.parse("reminderapp://reminder/${reminder.id}")
        }

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            reminder.id.toInt(),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            context,
            ReminderNotificationService.REMINDER_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_pen)
            .setContentTitle(context.getString(R.string.todo_reminder))
            .setContentText(context.getString(R.string.todo_reminder_time, reminder.title))
            .setAutoCancel(true)
            .setContentIntent(activityPendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(reminder.id.toInt(), notification)

    }
}
