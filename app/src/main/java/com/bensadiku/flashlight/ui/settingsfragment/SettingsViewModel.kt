package com.bensadiku.flashlight.ui.settingsfragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bensadiku.flashlight.database.FlashlightDatabaseDao
import kotlinx.coroutines.*

class SettingsViewModel(
    private val databaseDao: FlashlightDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * Updates the timer bool on the DB
     */
    fun updateEnableTimerBool(data: Boolean) {
        uiScope.launch {
            updateEnableTimer(data)
        }
    }

    private suspend fun updateEnableTimer(data: Boolean) {
        withContext(Dispatchers.IO) {
            databaseDao.updateEnableTimerBool(data)
        }
    }

    /**
     * Updates the row that determines if the user wants to use front or back light
     */
    fun updateLightTypeBool(data: Boolean) {
        uiScope.launch {
            updateLightType(data)
        }
    }

    private suspend fun updateLightType(data: Boolean) {
        withContext(Dispatchers.IO) {
            databaseDao.updateEnableFrontLightBool(data)
        }
    }

    /**
     * Updates the row that determines if the screen should stay awake or not
     */
    fun updateScreenAwakeBool(data: Boolean) {
        uiScope.launch {
            updateScreenAwake(data)
        }
    }

    private suspend fun updateScreenAwake(data: Boolean) {
        withContext(Dispatchers.IO) {
            databaseDao.updateEnableAwakeScreenBool(data)
        }
    }

    /**
     * Updates the timer on the DB
     */
    fun updateAddTimerLong(data: Long) {
        uiScope.launch {
            updateAddTimer(data)
        }
    }

    private suspend fun updateAddTimer(data: Long) {
        withContext(Dispatchers.IO) {
            databaseDao.updateAddTimerLong(data)
        }
    }

    /**
     * Update the notification boolean on the DB
     */

    fun updateNotificationBool(data: Boolean) {
        uiScope.launch {
            updateNotification(data)
        }
    }

    private suspend fun updateNotification(data: Boolean) {
        withContext(Dispatchers.IO) {
            databaseDao.updateEnableNotificationsBool(data)
        }
    }

    /**
     * Cancel all coroutines
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}