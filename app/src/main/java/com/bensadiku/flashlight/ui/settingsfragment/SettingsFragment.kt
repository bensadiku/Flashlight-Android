package com.bensadiku.flashlight.ui.settingsfragment


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceFragmentCompat
import com.bensadiku.flashlight.R
import androidx.preference.ListPreference
import androidx.preference.SwitchPreference
import com.bensadiku.flashlight.database.FlashlightDatabase

/**
 * A simple [Fragment] subclass.
 *
 *  mListPreference is a list of preferences, in my use-case it contains timers, 5 minutes, 10 minutes etc.
 */

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var mListPreference: ListPreference? = null
    private var settingsViewModel: SettingsViewModel? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_ui)

        mListPreference = preferenceScreen.findPreference(getString(R.string.add_timer_key)) as ListPreference
    }

    /**
     * If the preference is changed checks the key and inserts to the database using the viewmodel methods
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.add_timer_key) -> {
                mListPreference?.summary = "Current timer is " + mListPreference?.entry.toString()
                val num = mListPreference?.value?.toLong()!!
                settingsViewModel?.updateAddTimerLong(num)
            }
            getString(R.string.enable_timer_key) -> {
                val pref = findPreference(key) as SwitchPreference
                settingsViewModel?.updateEnableTimerBool(pref.isChecked)
            }
            getString(R.string.enable_front_light_key) -> {
                val pref = findPreference(key) as SwitchPreference
                settingsViewModel?.updateLightTypeBool(pref.isChecked)
            }
            getString(R.string.enable_screen_awake_key) -> {
                val pref = findPreference(key) as SwitchPreference
                settingsViewModel?.updateScreenAwakeBool(pref.isChecked)
            }
            getString(R.string.enable_notifications_key) -> {
                val pref = findPreference(key) as SwitchPreference
                settingsViewModel?.updateNotificationBool(pref.isChecked)
            }
        }
    }

    /**
     * Initialize the viewmodel with the factory
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        val application = requireNotNull(this.activity).application

        val flashlightDatabaseDao = FlashlightDatabase.getInstance(context).flashlightDatabaseDao()

        val viewModelFactory =
            SettingsViewModelFactory(flashlightDatabaseDao, application)

        settingsViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SettingsViewModel::class.java)
    }

    /**
     * OnResume we add the listener and update the description
     */
    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        // Setup the initial values
        mListPreference?.summary = "Current timer is " + mListPreference?.entry.toString()
    }

    /**
     * Remove the listener
     */
    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}
