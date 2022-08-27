package eamato.funn.r6companion.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import eamato.funn.r6companion.api.requests.NewsRequests
import eamato.funn.r6companion.paging.NewsDataSource
import eamato.funn.r6companion.utils.*
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    var currentNewsLocale: String? = null

    private val pAdditionalData = OneSourceMediatorLiveData<String?>()
    val additionalData: LiveData<String?> = pAdditionalData

    private var currentUpdates: Flow<PagingData<NewsDataMixedWithAds>>? = null
    private var currentNewsCategory: String? = null
    private var currentPager = Pager(
        config = PagingConfig(
            pageSize = NEWS_COUNT_DEFAULT_VALUE,
            prefetchDistance = NEWS_PREFETCH_DISTANCE,
            enablePlaceholders = true,
            initialLoadSize = NEWS_COUNT_DEFAULT_VALUE,
            maxSize = NEWS_MAX_PAGE_SIZE
        ),
        pagingSourceFactory = {
            NewsDataSource(
                newsRequests = NewsRequests.getNewsRequestCoroutines(),
                newsLocale = currentNewsLocale ?: DEFAULT_NEWS_LOCALE,
                newsCategory = currentNewsCategory,
                pref = sharedPreferences
            ).also {
                pAdditionalData.setSource(it.additionalLiveData) { data ->
                    pAdditionalData.value = data
                }
            }
        }
    )

    private fun getPager(
        newsLocale: String,
        newsCategory: String?
    ): Pager<Int, NewsDataMixedWithAds> {
        if (currentNewsLocale != newsLocale || currentNewsCategory != newsCategory) {
            currentPager = Pager(
                config = PagingConfig(
                    pageSize = NEWS_COUNT_DEFAULT_VALUE,
                    prefetchDistance = NEWS_PREFETCH_DISTANCE,
                    enablePlaceholders = true,
                    initialLoadSize = NEWS_COUNT_DEFAULT_VALUE,
                    maxSize = NEWS_MAX_PAGE_SIZE
                ),
                pagingSourceFactory = {
                    NewsDataSource(
                        newsRequests = NewsRequests.getNewsRequestCoroutines(),
                        newsLocale = newsLocale,
                        newsCategory = newsCategory,
                        pref = sharedPreferences
                    ).also {
                        pAdditionalData.setSource(it.additionalLiveData) { data ->
                            pAdditionalData.value = data
                        }
                    }
                }
            )

            currentNewsLocale = newsLocale
            currentNewsCategory = newsCategory
        }

        return currentPager
    }

    fun getUpdates(
        newsLocale: String,
        newsCategory: String?
    ): Flow<PagingData<NewsDataMixedWithAds>> {
        val lastResult = currentUpdates

        if (currentNewsLocale == newsLocale && currentNewsCategory == newsCategory && lastResult != null)
            return lastResult

        val updates = getPager(newsLocale, newsCategory).flow.cachedIn(viewModelScope)

        currentUpdates = updates

        return updates
    }
}