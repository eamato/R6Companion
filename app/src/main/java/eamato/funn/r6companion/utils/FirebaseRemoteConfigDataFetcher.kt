package eamato.funn.r6companion.utils

import androidx.lifecycle.asFlow
import eamato.funn.r6companion.firebase.things.OPERATORS
import eamato.funn.r6companion.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class FirebaseRemoteConfigDataFetcher(
    private val mainViewModel: MainViewModel
) : IRemoteDataFetcher {

    override suspend fun fetch(): String? {
        return withContext(Dispatchers.IO) {
            mainViewModel.observableFirebaseRemoteConfig.value?.getString(OPERATORS)
                ?: mainViewModel.observableFirebaseRemoteConfig.asFlow().firstOrNull()?.getString(OPERATORS)
        }
    }
}