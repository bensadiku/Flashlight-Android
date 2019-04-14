package com.bensadiku.flashlight.ui


import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.bensadiku.flashlight.R
import androidx.preference.ListPreference

/**
 * A simple [Fragment] subclass.
 *
 *  mListPreference is a list of preferences, in my use-case it contains timers, 5 minutes, 10 minutes etc.
 */

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var mListPreference: ListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_ui)

        mListPreference = preferenceScreen.findPreference(getString(R.string.add_timer_key)) as ListPreference
    }

    /**
     * If the preference is changed and it's the one that checks the timer time when update the description on the item
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.add_timer_key) -> {
                mListPreference?.summary = "Current timer is " + mListPreference?.entry.toString()
            }
        }
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
