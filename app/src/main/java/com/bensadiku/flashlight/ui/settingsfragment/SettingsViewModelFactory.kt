package com.bensadiku.flashlight.ui.settingsfragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bensadiku.flashlight.database.FlashlightDatabaseDao

/**
 * Provides the FlashlightDatabaseDao and context to the ViewModel.
 */
class SettingsViewModelFactory(
    private val flashlightDatabaseDao: FlashlightDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(flashlightDatabaseDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

