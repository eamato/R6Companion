package eamato.funn.r6companion.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.RouletteOperator
import eamato.funn.r6companion.firebase.things.COMING_SOON_KEY

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    private val pFirebaseRemoteConfig = MutableLiveData<FirebaseRemoteConfig>()
    val observableFirebaseRemoteConfig: LiveData<FirebaseRemoteConfig> = pFirebaseRemoteConfig

    val winnerCandidates = MutableLiveData<List<RouletteOperator>>(emptyList())

    private val pIlluminationLevel = MutableLiveData<Float?>(null)
    val illuminationLevel: LiveData<Float?> = pIlluminationLevel

    var applyIlluminationSensorValue = true

    init {
        firebaseRemoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings
                .Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
        )

        firebaseRemoteConfig.setDefaultsAsync(
            mapOf(COMING_SOON_KEY to application.getString(R.string.coming_soon))
        )

        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful)
                    pFirebaseRemoteConfig.value = firebaseRemoteConfig
            }

    }

    fun updateIlluminationLevel(currentIlluminationLevel: Float?) {
        pIlluminationLevel.value = currentIlluminationLevel
    }

}