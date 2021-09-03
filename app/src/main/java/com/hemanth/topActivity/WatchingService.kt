package com.hemanth.topActivity

import android.annotation.TargetApi
import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import com.hemanth.topActivity.SPHelper.isShowWindow
import com.hemanth.topActivity.TasksWindow.show
import java.util.*

class WatchingService : Service() {
    private val mHandler = Handler()
    private var mActivityManager: ActivityManager? = null
    private var text: String? = null
    private var timer: Timer? = null
    private var mNotiManager: NotificationManager? = null

    override fun onCreate() {
        super.onCreate()
        mActivityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        mNotiManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent): IBinder? = null


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (timer == null) {
            timer = Timer()
            timer?.scheduleAtFixedRate(refreshTask, 0, 500)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override fun onTaskRemoved(rootIntent: Intent) {
        Log.e("FLAGX : ", ServiceInfo.FLAG_STOP_WITH_TASK.toString() + "")
        val restartServiceIntent = Intent(
            applicationContext,
            this.javaClass
        )
        restartServiceIntent.setPackage(packageName)
        val restartServicePendingIntent = PendingIntent.getService(
            applicationContext, 1, restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val alarmService = applicationContext
            .getSystemService(ALARM_SERVICE) as AlarmManager
        alarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 500] =
            restartServicePendingIntent
        super.onTaskRemoved(rootIntent)
    }

    private val refreshTask = object : TimerTask() {
        override fun run() {
            val rtis = mActivityManager!!.getRunningTasks(1)
            val act = """
                ${rtis[0].topActivity!!.packageName}
                ${rtis[0].topActivity!!.className}
                """.trimIndent()
            if (act != text) {
                text = act
                if (isShowWindow(this@WatchingService)) {
                    mHandler.post { show(this@WatchingService, text) }
                }
            }
        }
    }
}