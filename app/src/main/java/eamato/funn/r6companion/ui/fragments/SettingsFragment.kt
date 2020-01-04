package eamato.funn.r6companion.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import eamato.funn.r6companion.R
import eamato.funn.r6companion.ui.fragments.abstracts.BasePreferenceFragment
import eamato.funn.r6companion.utils.*

private const val SCREEN_NAME = "Settings screen"

class SettingsFragment : BasePreferenceFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        hideIlluminationThresholdCalibrationIfNeeded()
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PREFERENCE_DARK_MODE_KEY -> {
                sharedPreferences.getDarkMode().setDarkMode()
                hideIlluminationThresholdCalibrationIfNeeded()
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            PREFERENCE_ABOUT_KEY -> {
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAboutFragment())
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }

    private fun hideIlluminationThresholdCalibrationIfNeeded() {
        findPreference<Preference>(PREFERENCE_ILLUMINATION_THRESHOLD_KEY)
            ?.let { nonNullIlluminationCalibration ->
                nonNullIlluminationCalibration.isVisible = preferenceManager
                    .sharedPreferences
                    .getString(
                        PREFERENCE_DARK_MODE_KEY, PREFERENCE_DARK_MODE_DEFAULT_VALUE
                    ) == PREFERENCE_DARK_MODE_VALUE_ADAPTIVE
            }
    }

}