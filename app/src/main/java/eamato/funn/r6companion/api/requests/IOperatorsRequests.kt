package eamato.funn.r6companion.api.requests

import eamato.funn.r6companion.api.ApiClient
import eamato.funn.r6companion.entities.R6StatsOperators
import eamato.funn.r6companion.utils.*
import retrofit2.http.GET

interface IOperatorsRequests {

    companion object {
        fun getR6StatsRequestCoroutines(): IOperatorsRequests = ApiClient
            .getApiClientCoroutines(R6STATS_HOST)
            .create(IOperatorsRequests::class.java)
    }

    @GET(OPERATORS_PATH)
    suspend fun getOperatorsCoroutines(): R6StatsOperators
}