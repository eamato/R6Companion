package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eamato.funn.r6companion.R
import eamato.funn.r6companion.firebase.things.COMING_SOON_TEXT
import kotlinx.android.synthetic.main.fragment_maps.*

class MapsFragment : FirebaseRemoteConfigFetcherFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onRemoteConfigFetchedSuccesfully() {
        tv_coming_soon_placeholder.text = firebaseRemoteConfig.getString(COMING_SOON_TEXT)
    }

}