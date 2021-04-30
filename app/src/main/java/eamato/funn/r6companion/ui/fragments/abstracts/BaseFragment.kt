package eamato.funn.r6companion.ui.fragments.abstracts

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import eamato.funn.r6companion.ui.activities.MainActivity
import eamato.funn.r6companion.utils.ILogScreenView
import eamato.funn.r6companion.viewmodels.MainViewModel

abstract class BaseFragment : Fragment(), ILogScreenView {

    protected val mainViewModel: MainViewModel by lazy {
        activity?.let { nonNullActivity ->
            ViewModelProvider(nonNullActivity).get(MainViewModel::class.java)
        } ?: throw Exception("Activity is null")
    }

    protected val mainActivity: MainActivity? by lazy {
        activity as MainActivity?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLiveDataObservers()
    }

    override fun onResume() {
        super.onResume()

        logScreenView()
    }

    @CallSuper
    protected fun logScreenView(className: String, screenName: String) {
        activity?.let {
            FirebaseAnalytics.getInstance(it).setCurrentScreen(it, className, screenName)
        }
    }

    abstract fun setLiveDataObservers()
    abstract fun onLiveDataObserversSet()

}