package eamato.funn.r6companion.ui.fragments.abstracts

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.analytics.FirebaseAnalytics
import eamato.funn.r6companion.utils.ILogScreenView
import eamato.funn.r6companion.viewmodels.MainViewModel

abstract class BaseFragment : Fragment(), ILogScreenView {

    protected val mainViewModel: MainViewModel by lazy {
        activity?.let { nonNullActivity ->
            ViewModelProviders.of(nonNullActivity).get(MainViewModel::class.java)
        } ?: throw Exception("Activity is null")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logScreenView()
    }

    protected fun logScreenView(className: String, screenName: String) {
        activity?.let {
            FirebaseAnalytics.getInstance(it).setCurrentScreen(it, className, screenName)
        }
    }

}