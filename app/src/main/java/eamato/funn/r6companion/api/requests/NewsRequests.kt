package eamato.funn.r6companion.api.requests

import eamato.funn.r6companion.api.ApiClient
import eamato.funn.r6companion.entities.News
import eamato.funn.r6companion.utils.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsRequests {

    companion object {
        fun getNewsRequest(): NewsRequests = ApiClient.getApiClient(NEWS_HOST).create(NewsRequests::class.java)
    }

    @GET(SOURCES_PATH)
    fun getNews(
        @Query(NEWS_COUNT_PARAM_KEY, encoded = true) newsCount: Int = NEWS_COUNT_DEFAULT_VALUE,
        @Query(NEWS_PAGE_PARAM_KEY, encoded = true) page: Int = 1
    ): Single<News>

}