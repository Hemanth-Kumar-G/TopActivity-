package com.hemanth.topActivity

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED
import android.content.*
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.hemanth.topActivity.SPHelper.isShowWindow
import com.hemanth.topActivity.SPHelper.setIsShowWindow
import com.hemanth.topActivity.SPHelper.setQSTileAdded
import com.hemanth.topActivity.TasksWindow.dismiss
import com.hemanth.topActivity.TasksWindow.show


const val ACTION_UPDATE_TITLE = "com.hemanth.topActivity.ACTION.UPDATE_TITLE"

@TargetApi(Build.VERSION_CODES.N)
class QuickSettingTileService : TileService() {

    private val mReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                updateTile()
            }
        }
    }

    override fun onTileAdded() {
        setQSTileAdded(this, true)
        sendBroadcast(Intent(ACTION_STATE_CHANGED))
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        setQSTileAdded(this, false)
        sendBroadcast(Intent(ACTION_STATE_CHANGED))
    }

    override fun onStartListening() {
        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_TITLE))
        super.onStartListening()
        updateTile()
    }

    override fun onStopListening() {
        unregisterReceiver(mReceiver)
        super.onStopListening()
    }

    override fun onClick() {
        if (WatchingAccessibilityService.getInstance() == null || !Settings.canDrawOverlays(this)) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(EXTRA_FROM_QS_TILE, true)
            startActivityAndCollapse(intent)
        } else {
            setIsShowWindow(this, !isShowWindow(this))
            if (isShowWindow(this)) {
                show(this, null)
                NotificationActionReceiver.showNotification(this, false)
            } else {
                dismiss(this)
                NotificationActionReceiver.showNotification(this, true)
            }
            sendBroadcast(Intent(ACTION_STATE_CHANGED))
        }
    }

    private fun updateTile() {
        if (WatchingAccessibilityService.getInstance() == null) {
            qsTile.state = Tile.STATE_INACTIVE
        } else {
            qsTile.state = if (isShowWindow(this)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
    }

    companion object {
        fun updateTile(context: Context) {
            requestListeningState(
                context.applicationContext,
                ComponentName(context, QuickSettingTileService::class.java)
            )
            context.sendBroadcast(Intent(ACTION_UPDATE_TITLE))
        }
    }
}