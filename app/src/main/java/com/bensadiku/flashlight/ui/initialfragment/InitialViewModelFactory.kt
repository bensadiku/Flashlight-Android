package com.bensadiku.flashlight.ui.initialfragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bensadiku.flashlight.database.FlashlightDatabaseDao

class InitialViewModelFactory(
    private val flashlightDatabaseDao: FlashlightDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InitialFragmentViewModel::class.java)) {
            return InitialFragmentViewModel(flashlightDatabaseDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
