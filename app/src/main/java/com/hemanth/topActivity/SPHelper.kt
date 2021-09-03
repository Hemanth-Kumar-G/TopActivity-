package com.hemanth.topActivity

import android.content.Context
import androidx.preference.PreferenceManager

object SPHelper {
    fun isShowWindow(context: Context?): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean("is_show_window", true)
    }

    fun setIsShowWindow(context: Context?, isShow: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp.edit().putBoolean("is_show_window", isShow).apply()
    }

    fun hasQSTileAdded(context: Context?): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean("has_qs_tile_added", false)
    }

    fun setQSTileAdded(context: Context?, added: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp.edit().putBoolean("has_qs_tile_added", added).apply()
    }

    fun isNotificationToggleEnabled(context: Context?): Boolean {
        if (!hasQSTileAdded(context)) {
            return true
        }
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean("is_noti_toggle_enabled", true)
    }

    fun setNotificationToggleEnabled(context: Context?, isEnabled: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp.edit().putBoolean("is_noti_toggle_enabled", isEnabled).apply()
    }
}