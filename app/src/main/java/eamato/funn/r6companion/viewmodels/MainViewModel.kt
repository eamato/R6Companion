package eamato.funn.r6companion.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.MobileAds
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.RouletteOperator
import eamato.funn.r6companion.firebase.things.COMING_SOON_KEY
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.resume

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettingsAsync(
            FirebaseRemoteConfigSettings
                .Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
        )

        setDefaultsAsync(
            mapOf(COMING_SOON_KEY to application.getString(R.string.coming_soon))
        )
    }

    private val pFirebaseRemoteConfig = MutableLiveData<FirebaseRemoteConfig>()
    val observableFirebaseRemoteConfig: LiveData<FirebaseRemoteConfig> = pFirebaseRemoteConfig

    val winnerCandidates = MutableLiveData<List<RouletteOperator>>(emptyList())

    private val _isLoadingSplash = MutableStateFlow(true)
    val isLoadingSplash = _isLoadingSplash.asStateFlow()

    private val pIlluminationLevel = MutableLiveData<Float?>(null)
    val illuminationLevel: LiveData<Float?> = pIlluminationLevel

    var applyIlluminationSensorValue = true

    init {
        initializeApp()
    }

    fun updateIlluminationLevel(currentIlluminationLevel: Float?) {
        pIlluminationLevel.value = currentIlluminationLevel
    }

    private fun initializeApp() {
        viewModelScope.launch {
            val remoteConfigRequest = async { fetchRemoteConfig() }
            val mobileAdsSDKInitializationRequest = async { initializeMobilAdsSDK() }
            try {
                withTimeout(5 * 1_000L) {
                    val firebaseRemoteConfigResult = remoteConfigRequest.await()
                    pFirebaseRemoteConfig.value = firebaseRemoteConfigResult
                    mobileAdsSDKInitializationRequest.await()

                    _isLoadingSplash.value = false
                }
            } catch (e: Exception) {
                if (e is TimeoutCancellationException)
                    Log.d("Splash", "Something reached timeout")
            } finally {
                _isLoadingSplash.value = false
            }
        }
    }

    private suspend fun fetchRemoteConfig() = withContext(Dispatchers.IO) {
        return@withContext _fetchRemoteConfig()
    }

    private suspend fun _fetchRemoteConfig(): FirebaseRemoteConfig = suspendCancellableCoroutine { continuation ->
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                continuation.resume(firebaseRemoteConfig)
            }
    }

    private suspend fun initializeMobilAdsSDK() = withContext(Dispatchers.IO) {
        return@withContext MobileAds.initialize(getApplication())
    }
}