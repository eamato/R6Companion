package eamato.funn.r6companion.utils

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import eamato.funn.r6companion.firebase.things.OPERATORS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class FirebaseRemoteConfigDataFetcher : IRemoteDataFetcher {

    private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
        setConfigSettingsAsync(
            FirebaseRemoteConfigSettings
                .Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
        )
    }

    override suspend fun fetch(): String? {
       return withContext(Dispatchers.IO) {
           var isResultReceived = false
           var result: String? = null
           firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener {
               isResultReceived = true
               if (it.isSuccessful)
                   result = firebaseRemoteConfig.getString(OPERATORS)
           }
           while (isResultReceived.not()) {
               delay(1 * 1000L)
           }
           result
        }
    }
}