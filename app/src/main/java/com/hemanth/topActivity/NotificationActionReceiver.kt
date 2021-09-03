package com.hemanth.topActivity

import android.app.ActivityManager
import android.app.ActivityManager.RunningTaskInfo
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

const val ACTION_PAUSE = 0
const val ACTION_RESUME = 1
const val ACTION_STOP = 2
const val NOTIFICATION_ID = 1
const val EXTRA_NOTIFICATION_ACTION = "command"
const val ACTION_NOTIFICATION_RECEIVER = "com.hemanth.topActivity.ACTION_NOTIFICATION_RECEIVER"

class NotificationActionReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {

        when (intent.getIntExtra(EXTRA_NOTIFICATION_ACTION, -1)) {
            ACTION_RESUME -> {
                showNotification(context, false)
                SPHelper.setIsShowWindow(context, true)
                val lollipop = Build.VERSION.SDK_INT >= 21
                if (!lollipop) {
                    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    val rtis = am.getRunningTasks(1)
                    val act = """
                        ${rtis[0].topActivity!!.packageName}
                        ${rtis[0].topActivity!!.className}
                        """.trimIndent()
                    TasksWindow.show(context, act)
                } else {
                    TasksWindow.show(context, null)
                }
            }
            ACTION_PAUSE -> {
                showNotification(context, true)
                TasksWindow.dismiss(context)
                SPHelper.setIsShowWindow(context, false)
            }
            ACTION_STOP -> {
                TasksWindow.dismiss(context)
                SPHelper.setIsShowWindow(context, false)
                cancelNotification(context)
            }
        }
    }

    companion object {
        fun showNotification(context: Context, isPaused: Boolean) {
            if (!SPHelper.isNotificationToggleEnabled(context)) return
            val pIntent =
                PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
            val builder = NotificationCompat.Builder(context, "channel")
                .setContentTitle(
                    context.getString(
                        R.string.is_running, context.getString(R.string.app_name)
                    )
                )
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(context.getString(R.string.touch_to_open))
                .setColor(Color.GREEN)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setOngoing(!isPaused)

            if (isPaused) {
                builder.addAction(
                    R.drawable.ic_noti_action_resume,
                    context.getString(R.string.noti_action_resume),
                    getPendingIntent(context, ACTION_RESUME)
                )
            } else {
                builder.addAction(
                    R.drawable.ic_noti_action_pause,
                    context.getString(R.string.noti_action_pause),
                    getPendingIntent(context, ACTION_PAUSE)
                )
            }
            builder.addAction(
                R.drawable.ic_noti_action_stop,
                context.getString(R.string.noti_action_stop),
                getPendingIntent(context, ACTION_STOP)
            )
                .setContentIntent(pIntent)

            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(NOTIFICATION_ID, builder.build())
        }

        fun getPendingIntent(context: Context?, command: Int): PendingIntent? {
            val intent = Intent(ACTION_NOTIFICATION_RECEIVER)
            intent.putExtra(EXTRA_NOTIFICATION_ACTION, command)
            return PendingIntent.getBroadcast(context, command, intent, 0)
        }

        fun cancelNotification(context: Context) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(NOTIFICATION_ID)
        }
    }
}