package com.hemanth.topActivity

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class WatchingAccessibilityService : AccessibilityService() {

    companion object {
        private var sInstance: WatchingAccessibilityService? = null
        fun getInstance() = sInstance
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (SPHelper.isShowWindow(this)) {
                TasksWindow.show(
                    this, """
     ${event.packageName}
     ${event.className}
     """.trimIndent()
                )
            }
        }
    }

    override fun onInterrupt() = Unit

    override fun onServiceConnected() {
        sInstance = this
        if (SPHelper.isShowWindow(this)) {
            NotificationActionReceiver.showNotification(this, false)
        }
        sendBroadcast(Intent(ACTION_UPDATE_TITLE))
        super.onServiceConnected()
    }


    override fun onUnbind(intent: Intent?): Boolean {
        sInstance = null
        TasksWindow.dismiss(this)
        NotificationActionReceiver.cancelNotification(this)
        sendBroadcast(Intent(ACTION_UPDATE_TITLE))
        return super.onUnbind(intent)
    }
}