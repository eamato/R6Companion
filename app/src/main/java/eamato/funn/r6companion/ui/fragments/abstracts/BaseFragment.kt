package eamato.funn.r6companion.ui.fragments.abstracts

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import eamato.funn.r6companion.viewmodels.MainViewModel

abstract class BaseFragment : Fragment() {

    protected val mainViewModel: MainViewModel by lazy {
        activity?.let { nonNullActivity ->
            ViewModelProviders.of(nonNullActivity).get(MainViewModel::class.java)
        } ?: throw Exception("Activity is null")
    }

}