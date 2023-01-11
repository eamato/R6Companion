package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eamato.funn.r6companion.R
import eamato.funn.r6companion.databinding.FragmentMapsBinding
import eamato.funn.r6companion.firebase.things.COMING_SOON_KEY
import eamato.funn.r6companion.firebase.things.ComingSoon
import eamato.funn.r6companion.ui.fragments.abstracts.BaseCompanionFragment
import eamato.funn.r6companion.utils.getFirebaseRemoteConfigEntity
import eamato.funn.r6companion.utils.getText

private const val SCREEN_NAME = "Maps screen"

class MapsFragment : BaseCompanionFragment() {

    private var fragmentMapsBinding: FragmentMapsBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMapsBinding = FragmentMapsBinding.inflate(inflater, container, false)
        return fragmentMapsBinding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        fragmentMapsBinding = null
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        mainViewModel.observableFirebaseRemoteConfig.observe(this) {
            it?.let { nonNullFirebaseRemoteConfig ->
                nonNullFirebaseRemoteConfig.getString(COMING_SOON_KEY)
                    .getFirebaseRemoteConfigEntity(ComingSoon::class.java)
                    ?.let { nonNullComingSoon ->
                        context?.let { nonNullContext ->
                            fragmentMapsBinding?.tvComingSoonPlaceholder?.text =
                                nonNullComingSoon.getText(nonNullContext)
                        }
                    }
            }
        }
    }

    override fun onLiveDataObserversSet() {

    }

    override fun getFragmentsTitle(): Int {
        return R.string.maps_fragment_label
    }

    override fun getFragmentsIcon(): Int {
        return R.drawable.ic_map_24dp
    }

}