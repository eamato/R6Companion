package eamato.funn.r6companion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import eamato.funn.r6companion.api.requests.NewsRequests
import eamato.funn.r6companion.paging.NewsDataSource
import eamato.funn.r6companion.utils.DEFAULT_NEWS_LOCALE
import eamato.funn.r6companion.utils.NEWS_COUNT_DEFAULT_VALUE
import eamato.funn.r6companion.utils.NewsDataMixedWithAds
import kotlinx.coroutines.flow.Flow

class HomeViewModel : ViewModel() {

    private var currentUpdates: Flow<PagingData<NewsDataMixedWithAds>>? = null
    private var currentNewsLocale: String? = null
    private var currentPager = Pager(
        config = PagingConfig(
            pageSize = NEWS_COUNT_DEFAULT_VALUE / 2,
            prefetchDistance = NEWS_COUNT_DEFAULT_VALUE / 2,
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            NewsDataSource(
                NewsRequests.getNewsRequestCoroutines(),
                currentNewsLocale ?: DEFAULT_NEWS_LOCALE
            )
        }
    )

    private fun getPager(newsLocale: String): Pager<Int, NewsDataMixedWithAds> {
        if (currentNewsLocale != newsLocale) {
            currentPager = Pager(
                config = PagingConfig(
                    pageSize = NEWS_COUNT_DEFAULT_VALUE / 2,
                    prefetchDistance = NEWS_COUNT_DEFAULT_VALUE / 4,
                    enablePlaceholders = true
                ),
                pagingSourceFactory = {
                    NewsDataSource(
                        NewsRequests.getNewsRequestCoroutines(),
                        newsLocale
                    )
                }
            )
            currentNewsLocale = newsLocale
        }
        return currentPager
    }

    fun getUpdates(newsLocale: String): Flow<PagingData<NewsDataMixedWithAds>> {
        val lastResult = currentUpdates
        if (currentNewsLocale == newsLocale && lastResult != null)
            return lastResult
        val updates = getPager(newsLocale).flow.cachedIn(viewModelScope)
        currentUpdates = updates
        return updates
    }

}