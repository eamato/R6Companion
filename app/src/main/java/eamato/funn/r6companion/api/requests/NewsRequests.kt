package eamato.funn.r6companion.api.requests

import eamato.funn.r6companion.api.ApiClient
import eamato.funn.r6companion.entities.Updates
import eamato.funn.r6companion.utils.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsRequests {

    companion object {
        fun getNewsRequest(): NewsRequests = ApiClient.getApiClient(NEWS_HOST).create(NewsRequests::class.java)
    }

    @GET(NEWS_PATH)
    fun getNews(
        @Query(NEWS_SKIP_PARAM_KEY, encoded = true) skip: Int = 0,
        @Query(NEWS_COUNT_PARAM_KEY, encoded = true) newsCount: Int = NEWS_COUNT_DEFAULT_VALUE,
        @Query(NEWS_LOCALE_PARAM_KEY, encoded = true) newsLocale: String = "en-us",
        @Query(NEWS_TAG_PARAM_KEY, encoded = true) newsTag: String = NEWS_TAG_PARAM_R6_VALUE,
        @Query(NEWS_CATEGORIES_FILTER_PARAM_KEY, encoded = true) newsCategoriesFilter: String = NEWS_CATEGORIES_FILTER_PARAM_NEWS_VALUE
    ): Single<Updates>

    @GET(NEWS_PATH)
    fun getUpdates(
        @Query(NEWS_SKIP_PARAM_KEY, encoded = true) skip: Int = 0,
        @Query(NEWS_COUNT_PARAM_KEY, encoded = true) newsCount: Int = NEWS_COUNT_DEFAULT_VALUE,
        @Query(NEWS_LOCALE_PARAM_KEY, encoded = true) newsLocale: String = "en-us",
        @Query(NEWS_TAG_PARAM_KEY, encoded = true) newsTag: String = NEWS_TAG_PARAM_R6_VALUE
    ): Single<Updates>

}