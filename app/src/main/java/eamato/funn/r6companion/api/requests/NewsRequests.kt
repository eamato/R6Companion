package eamato.funn.r6companion.api.requests

import eamato.funn.r6companion.api.ApiClient
import eamato.funn.r6companion.entities.Updates
import eamato.funn.r6companion.utils.*
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsRequests {

    companion object {
        fun getNewsRequestCoroutines(): NewsRequests = ApiClient
            .getApiClientCoroutines(NEWS_HOST)
            .create(NewsRequests::class.java)

//        fun getNewsRequestCoroutines(): NewsRequests = ApiClient
//            .getApiClientCoroutines("http://192.168.0.7:8001")
//            .create(NewsRequests::class.java)
    }

    @Headers(NEWS_AUTHORIZATION_TOKEN_HEADER)
    @GET(NEWS_PATH)
    suspend fun getUpdatesCoroutines(
        @Query(NEWS_SKIP_PARAM_KEY, encoded = true) skip: Int = 0,
        @Query(NEWS_COUNT_PARAM_KEY, encoded = true) newsCount: Int = NEWS_COUNT_DEFAULT_VALUE,
        @Query(NEWS_LOCALE_PARAM_KEY, encoded = true) newsLocale: String = DEFAULT_NEWS_LOCALE,
        @Query(NEWS_TAG_PARAM_KEY, encoded = true) newsTag: String = NEWS_TAG_PARAM_R6_VALUE,
        @Query(NEWS_CATEGORIES_FILTER_PARAM_KEY, encoded = true) newsCategoriesFilter: String? = null
    ): Updates

//    @GET("api/news")
//    suspend fun getUpdatesCoroutines(
//        @Query(NEWS_SKIP_PARAM_KEY, encoded = true) skip: Int = 0,
//        @Query(NEWS_COUNT_PARAM_KEY, encoded = true) newsCount: Int = NEWS_COUNT_DEFAULT_VALUE,
//        @Query(NEWS_CATEGORIES_FILTER_PARAM_KEY, encoded = true) newsCategoriesFilter: String? = null
//    ): Updates
}