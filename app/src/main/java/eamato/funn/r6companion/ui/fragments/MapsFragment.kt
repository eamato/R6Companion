package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import eamato.funn.r6companion.R
import eamato.funn.r6companion.firebase.things.COMING_SOON_KEY
import eamato.funn.r6companion.firebase.things.ComingSoon
import eamato.funn.r6companion.ui.fragments.abstracts.BaseCompanionFragment
import eamato.funn.r6companion.utils.getFirebaseRemoteConfigEntity
import eamato.funn.r6companion.utils.getText
import kotlinx.android.synthetic.main.fragment_maps.*

private const val SCREEN_NAME = "Maps screen"

class MapsFragment : BaseCompanionFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.observableFirebaseRemoteConfig.observe(this, Observer {
            it?.let { nonNullFirebaseRemoteConfig ->
                nonNullFirebaseRemoteConfig.getString(COMING_SOON_KEY)
                    .getFirebaseRemoteConfigEntity(ComingSoon::class.java)?.let { nonNullComingSoon ->
                        context?.let { nonNullContext ->
                            tv_coming_soon_placeholder?.text = nonNullComingSoon.getText(nonNullContext)
                        }
                    }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun getFragmentsTitle(): Int {
        return R.string.maps_fragment_label
    }

    override fun getFragmentsIcon(): Int {
        return R.drawable.ic_map_24dp
    }

}