package com.hemanth.topActivity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import java.lang.Exception

@SuppressLint("StaticFieldLeak")
object TasksWindow {
    private var sWindowParams: WindowManager.LayoutParams? = null
    private var sWindowManager: WindowManager? = null
    private var sView: View? = null

    fun init(context: Context) {
        sWindowManager =
            context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        sWindowParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, 0x18, PixelFormat.TRANSLUCENT
        )
        sWindowParams?.gravity = Gravity.LEFT + Gravity.BOTTOM
        sView = LayoutInflater.from(context).inflate(
            R.layout.window_tasks, null
        )
    }

    fun show(context: Context, text: String?) {
        if (sWindowManager == null) init(context)

        val textView = sView?.findViewById<View>(R.id.text) as TextView
        textView.text = text
        try {
            sWindowManager?.addView(sView, sWindowParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            QuickSettingTileService.updateTile(context)
    }

    fun dismiss(context: Context) {
        try {
            sWindowManager?.removeView(sView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            QuickSettingTileService.updateTile(context)
    }

}