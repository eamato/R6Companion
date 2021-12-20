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
    var currentNewsLocale: String? = null
    private var currentNewsCategory: String? = null
    private var currentPager = Pager(
        config = PagingConfig(
            pageSize = NEWS_COUNT_DEFAULT_VALUE / 2,
            prefetchDistance = NEWS_COUNT_DEFAULT_VALUE / 2,
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            NewsDataSource(
                newsRequests = NewsRequests.getNewsRequestCoroutines(),
                newsLocale = currentNewsLocale ?: DEFAULT_NEWS_LOCALE,
                newsCategory = currentNewsCategory
            )
        }
    )

    private fun getPager(
        newsLocale: String,
        newsCategory: String?
    ): Pager<Int, NewsDataMixedWithAds> {
        if (currentNewsLocale != newsLocale || currentNewsCategory != newsCategory) {
            currentPager = Pager(
                config = PagingConfig(
                    pageSize = NEWS_COUNT_DEFAULT_VALUE / 2,
                    prefetchDistance = NEWS_COUNT_DEFAULT_VALUE / 4,
                    enablePlaceholders = true
                ),
                pagingSourceFactory = {
                    NewsDataSource(
                        newsRequests = NewsRequests.getNewsRequestCoroutines(),
                        newsLocale = newsLocale,
                        newsCategory = newsCategory
                    )
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