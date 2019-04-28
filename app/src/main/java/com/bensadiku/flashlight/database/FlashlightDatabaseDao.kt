package com.bensadiku.flashlight.database

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Data Access Object
 * Self explanatory name methods.
 */
@Dao
interface FlashlightDatabaseDao {

    @Insert
    fun insert(flashlightEntity: FlashlightEntity)

    @Update
    fun update(flashlightEntity: FlashlightEntity)

    @Delete
    fun delete(flashlightEntity: FlashlightEntity)

    @Query("SELECT * FROM flashlight_preferences ORDER BY Id DESC LIMIT 1")
    fun getPref(): LiveData<FlashlightEntity?>

    @Query("UPDATE FLASHLIGHT_PREFERENCES SET enable_timer_key = :value")
    fun updateEnableTimerBool(value:Boolean)

    @Query("UPDATE FLASHLIGHT_PREFERENCES SET enable_front_light_key = :value")
    fun updateEnableFrontLightBool(value:Boolean)

    @Query("UPDATE FLASHLIGHT_PREFERENCES SET enable_notifications_key = :value")
    fun updateEnableNotificationsBool(value:Boolean)

    @Query("UPDATE FLASHLIGHT_PREFERENCES SET enable_screen_awake_key = :value")
    fun updateEnableAwakeScreenBool(value:Boolean)

    @Query("UPDATE FLASHLIGHT_PREFERENCES SET add_timer_key = :value")
    fun updateAddTimerLong(value:Long)
}