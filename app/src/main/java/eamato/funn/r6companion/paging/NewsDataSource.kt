package eamato.funn.r6companion.paging

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import eamato.funn.r6companion.api.requests.NewsRequests
import eamato.funn.r6companion.entities.Updates
import eamato.funn.r6companion.utils.NEWS_COUNT_DEFAULT_VALUE
import eamato.funn.r6companion.utils.NewsDataMixedWithAds
import eamato.funn.r6companion.utils.toNewsMixedWithAds
import kotlinx.coroutines.*

class NewsDataSource(
    private val pref: SharedPreferences,
    private val newsRequests: NewsRequests,
    private val newsLocale: String,
    private val newsCategory: String? = null
) : PagingSource<Int, NewsDataMixedWithAds>() {

    private val pAdditionalLiveData = MutableLiveData<String?>(null)
    val additionalLiveData = pAdditionalLiveData

    override fun getRefreshKey(state: PagingState<Int, NewsDataMixedWithAds>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null

        return (page.prevKey?.plus(state.config.pageSize)
            ?: page.nextKey?.minus(state.config.pageSize))
                .also { Log.d("Paging", "refreshKey = $it") }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsDataMixedWithAds> {
        val skip = params.key ?: 0
        val newsCount = params.loadSize

        return try {
            val response = newsRequests.getUpdatesCoroutines(
                skip = skip,
                newsLocale = newsLocale,
                newsCategoriesFilter = newsCategory,
                newsCount = newsCount
            )

            additionalLiveData.value = response.toString()

            val updates = response.items ?: emptyList()
            val result = parseResult(updates)

            val prevKey = if (skip == 0)
                null
            else
                skip - newsCount

            val nextKey = if (updates.size < newsCount)
                null
            else
                skip + newsCount

            LoadResult.Page(result, prevKey, nextKey)
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

    private suspend fun parseResult(updates: List<Updates.Item?>): List<NewsDataMixedWithAds> {
        return withContext(Dispatchers.IO) {
            updates.toNewsMixedWithAds(pref)
        }
    }
}