package com.bensadiku.flashlight

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

/**
 * Top class Flashlight will set the dark mode before anything else loads
 * Will also create the notification channel if the Build.VERSION >= O
 */
class Flashlight : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES)

        createNotificationChannels()
    }

    companion object {
       const val TIMER_CHANNEL_1 = "TIMER_CHANNEL_1"
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(TIMER_CHANNEL_1, "Timer channel", NotificationManager.IMPORTANCE_HIGH)
            channel1.lightColor = (R.color.colorAccent)
            channel1.description = "Timer Channel"

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel1)
        }
    }
}