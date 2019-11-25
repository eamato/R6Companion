package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import eamato.funn.r6companion.R
import eamato.funn.r6companion.firebase.things.COMING_SOON_TEXT
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import kotlinx.android.synthetic.main.fragment_weapons.*

class WeaponsFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.observableFirebaseRemoteConfig.observe(this, Observer {
            it?.let { nonNullFirebaseRemoteConfig ->
                tv_coming_soon_placeholder.text = nonNullFirebaseRemoteConfig.getString(COMING_SOON_TEXT)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weapons, container, false)
    }

}