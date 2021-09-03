package com.hemanth.topActivity

import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

const val EXTRA_FROM_QS_TILE = "from_qs_tile"
const val ACTION_STATE_CHANGED = "com.hemanth.topActivity.ACTION_STATE_CHANGED"

class MainActivity : AppCompatActivity() {


    private val switch: SwitchCompat by lazy { findViewById(R.id.sw_window) }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            refreshWindowSwitch()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && buttonView === switch) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && !Settings.canDrawOverlays(
                        this
                    )
                ) {
                    AlertDialog.Builder(this)
                        .setMessage(R.string.dialog_enable_overlay_window_msg)
                        .setPositiveButton(R.string.dialog_enable_overlay_window_positive_btn)
                        { dialog, _ ->
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                            intent.data = Uri.parse("package:$packageName")
                            startActivity(intent)
                            dialog.dismiss()
                        }.setNegativeButton(android.R.string.cancel) { _, _ ->
                            SPHelper.setIsShowWindow(this@MainActivity, false)
                            refreshWindowSwitch()
                        }.setOnCancelListener {
                            SPHelper.setIsShowWindow(this@MainActivity, false)
                            refreshWindowSwitch()
                        }
                        .create()
                        .show()
                    buttonView.isChecked = false
                    return@setOnCheckedChangeListener
                } else if (WatchingAccessibilityService.getInstance() == null) {
                    AlertDialog.Builder(this)
                        .setMessage(R.string.dialog_enable_accessibility_msg)
                        .setPositiveButton(R.string.dialog_enable_accessibility_positive_btn) { _, _ ->
                            SPHelper.setIsShowWindow(this@MainActivity, true)
                            val intent = Intent()
                            intent.action = "android.settings.ACCESSIBILITY_SETTINGS"
                            startActivity(intent)
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> refreshWindowSwitch() }
                        .setOnCancelListener { refreshWindowSwitch() }
                        .create()
                        .show()
                    SPHelper.setIsShowWindow(this, true)
                    return@setOnCheckedChangeListener
                }
            }

                SPHelper.setIsShowWindow(this, isChecked)
                if (!isChecked) {
                    TasksWindow.dismiss(this)
                } else {
                    TasksWindow.show(
                        this, """$packageName
     ${javaClass.name}
     """.trimIndent()
                    )
                }
            }


        TasksWindow.show(this, "")
        startService(Intent(this, WatchingService::class.java))
        if (intent.getBooleanExtra(EXTRA_FROM_QS_TILE, false)) {
            switch.isChecked = true
            registerReceiver(mReceiver, IntentFilter(ACTION_STATE_CHANGED))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (getIntent().getBooleanExtra(EXTRA_FROM_QS_TILE, false)) {
            switch.isChecked = true
        }
    }

    override fun onResume() {
        super.onResume()
        refreshWindowSwitch()
        NotificationActionReceiver.cancelNotification(this)
    }

    override fun onPause() {
        super.onPause()
        if (SPHelper.isShowWindow(this) && WatchingAccessibilityService.getInstance() != null) {
            NotificationActionReceiver.showNotification(this, false)
        }
    }

    private fun refreshWindowSwitch() {
        switch.isChecked = SPHelper.isShowWindow(this)
        if (WatchingAccessibilityService.getInstance() == null) {
            switch.isChecked = false
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }
}   