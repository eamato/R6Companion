package eamato.funn.r6companion.ui.fragments.abstracts

import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.analytics.FirebaseAnalytics
import eamato.funn.r6companion.utils.ILogScreenView
import eamato.funn.r6companion.viewmodels.MainViewModel

abstract class BasePreferenceFragment : PreferenceFragmentCompat(), ILogScreenView,
    SharedPreferences.OnSharedPreferenceChangeListener {

    protected val mainViewModel: MainViewModel by lazy {
        activity?.let { nonNullActivity ->
            ViewModelProvider(nonNullActivity)[MainViewModel::class.java]
        } ?: throw Exception("Activity is null")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logScreenView()
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    protected fun logScreenView(className: String, screenName: String) {
        activity?.let {
            FirebaseAnalytics.getInstance(it).setCurrentScreen(it, className, screenName)
        }
    }
}