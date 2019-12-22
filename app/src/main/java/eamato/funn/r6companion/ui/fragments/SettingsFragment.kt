package eamato.funn.r6companion.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import eamato.funn.r6companion.R
import eamato.funn.r6companion.ui.fragments.abstracts.BasePreferenceFragment
import eamato.funn.r6companion.utils.isDarkModeEnabled
import eamato.funn.r6companion.utils.setDarkMode

private const val SCREEN_NAME = "Settings screen"

class SettingsFragment : BasePreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        sharedPreferences.isDarkModeEnabled().setDarkMode()
    }

}