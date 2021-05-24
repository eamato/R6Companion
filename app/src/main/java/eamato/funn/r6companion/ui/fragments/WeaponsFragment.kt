package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eamato.funn.r6companion.R
import eamato.funn.r6companion.databinding.FragmentWeaponsBinding
import eamato.funn.r6companion.firebase.things.COMING_SOON_KEY
import eamato.funn.r6companion.firebase.things.ComingSoon
import eamato.funn.r6companion.ui.fragments.abstracts.BaseCompanionFragment
import eamato.funn.r6companion.utils.getFirebaseRemoteConfigEntity
import eamato.funn.r6companion.utils.getText

private const val SCREEN_NAME = "Weapons screen"

class WeaponsFragment : BaseCompanionFragment() {

    private var fragmentWeaponsBinding: FragmentWeaponsBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentWeaponsBinding = FragmentWeaponsBinding.inflate(inflater, container, false)
        return fragmentWeaponsBinding?.root
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        mainViewModel.observableFirebaseRemoteConfig.observe(this, {
            it?.let { nonNullFirebaseRemoteConfig ->
                nonNullFirebaseRemoteConfig.getString(COMING_SOON_KEY)
                    .getFirebaseRemoteConfigEntity(ComingSoon::class.java)?.let { nonNullComingSoon ->
                        context?.let { nonNullContext ->
                            fragmentWeaponsBinding?.tvComingSoonPlaceholder?.text = nonNullComingSoon.getText(nonNullContext)
                        }
                    }
            }
        })
    }

    override fun onLiveDataObserversSet() {

    }

    override fun getFragmentsTitle(): Int {
        return R.string.weapons_fragment_label
    }

    override fun getFragmentsIcon(): Int {
        return R.drawable.ic_weapons_24dp
    }

}