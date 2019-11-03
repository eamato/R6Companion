package eamato.funn.r6companion.api.requests

import eamato.funn.r6companion.api.ApiClient
import eamato.funn.r6companion.utils.API_KEY_PARAM_KEY
import eamato.funn.r6companion.utils.API_KEY_PARAM_VALUE
import eamato.funn.r6companion.utils.NEWS_HOST
import eamato.funn.r6companion.utils.SOURCES_PATH
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsRequests {

    companion object {
        fun getNewsRequest() = ApiClient.getApiClient(NEWS_HOST).create(NewsRequests::class.java)
    }

    @GET(SOURCES_PATH)
    fun getNews(
        @Query(API_KEY_PARAM_KEY, encoded = true) value: String = API_KEY_PARAM_VALUE
    ): Single<Any>

}