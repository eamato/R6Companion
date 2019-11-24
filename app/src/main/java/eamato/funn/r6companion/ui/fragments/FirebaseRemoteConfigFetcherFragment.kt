package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import eamato.funn.r6companion.R
import eamato.funn.r6companion.firebase.things.IRemoteConfigFetcher

abstract class FirebaseRemoteConfigFetcherFragment : BaseFragment(), IRemoteConfigFetcher {

    protected val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchRemoteConfig()
    }

    override fun fetchRemoteConfig() {
        firebaseRemoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
        )

        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    onRemoteConfigFetchedSuccesfully()
            }
    }

    abstract fun onRemoteConfigFetchedSuccesfully()

}