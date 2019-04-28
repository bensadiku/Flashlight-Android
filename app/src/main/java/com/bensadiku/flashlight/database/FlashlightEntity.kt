package com.bensadiku.flashlight.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashlight_preferences")
data class FlashlightEntity(

    //Only 1 entry will be inserted, no need to generate primary keys
    @PrimaryKey(autoGenerate = false)
    var Id: Int = 1,

    // To save the data if it should display the timer
    @ColumnInfo(name = "enable_timer_key")
    var enableTimer: Boolean = false,

    //To save the timer in Long
    @ColumnInfo(name = "add_timer_key")
    var addTimer: Long = 300000,

    //To save the data if the front of back light needs to be on
    @ColumnInfo(name = "enable_front_light_key")
    var lightType: Boolean = false,

    //To save the data if screen should stay awake
    @ColumnInfo(name = "enable_screen_awake_key")
    var screenAwake: Boolean = false,

    //To save the data if screen should stay awake
    @ColumnInfo(name = "enable_notifications_key")
    var enableNotifications: Boolean = true
)