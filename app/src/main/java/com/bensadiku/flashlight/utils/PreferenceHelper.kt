package com.bensadiku.flashlight.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.bensadiku.flashlight.R


/**
 * Preference helper class to get the values from the saved preferences
 *
 * Update: No longer in use, Room database will substitute this helper, will leave it here for testing purposes
 */
class PreferenceHelper(private val context: Context) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    enum class BooleanPref(val prefId: Int, val defaultId: Int) {
        EnableTimer(R.string.enable_timer_key, R.bool.enable_timer_default),
        PickLight(R.string.enable_front_light_key,R.bool.enable_front_light_default),
        KeepAwake(R.string.enable_screen_awake_key,R.bool.enable_screen_awake_default),
        SendNotifications(R.string.enable_notifications_key,R.bool.enable_notifications_default)
    }

    enum class Timer(val prefId: Int, val defaultId: Int) {
        AddTimer(R.string.add_timer_key, R.string.add_timer_default)
    }

    fun getTimer(pref: Timer) = prefs.getString(context.getString(pref.prefId), context.resources.getString(pref.defaultId)
    )

    fun getBooleanPref(pref: BooleanPref) =
        prefs.getBoolean(context.getString(pref.prefId), context.resources.getBoolean(pref.defaultId))

    fun setBooleanPref(pref: BooleanPref, value: Boolean) =
        prefs.edit().putBoolean(context.getString(pref.prefId), value).commit()
}