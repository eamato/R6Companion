package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import eamato.funn.r6companion.R
import eamato.funn.r6companion.firebase.things.OUR_MISSION_KEY
import eamato.funn.r6companion.firebase.things.OurMission
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import eamato.funn.r6companion.utils.getFirebaseRemoteConfigEntity
import eamato.funn.r6companion.utils.getText
import kotlinx.android.synthetic.main.fragment_about.*

private const val SCREEN_NAME = "About screen"

class AboutFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        mainViewModel.observableFirebaseRemoteConfig.observe(this, Observer {
            it?.let { nonNullFirebaseRemoteConfig ->
                nonNullFirebaseRemoteConfig.getString(OUR_MISSION_KEY)
                    .getFirebaseRemoteConfigEntity(OurMission::class.java)?.let { nonNullOurMission ->
                        context?.let { nonNullContext ->
                            tv_our_mission?.text = nonNullOurMission.getText(nonNullContext)
                        }
                    }
            }
        })
    }

    override fun onLiveDataObserversSet() {

    }

}