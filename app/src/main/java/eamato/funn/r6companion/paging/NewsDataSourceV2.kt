package eamato.funn.r6companion.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import eamato.funn.r6companion.api.requests.NewsRequests
import eamato.funn.r6companion.entities.Updates
import eamato.funn.r6companion.utils.NEWS_COUNT_DEFAULT_VALUE
import eamato.funn.r6companion.utils.NewsDataMixedWithAds
import eamato.funn.r6companion.utils.toNewsMixedWithAds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsDataSourceV2(
    private val newsRequests: NewsRequests,
    private val newsLocale: String
) : PagingSource<Int, NewsDataMixedWithAds>() {

    override fun getRefreshKey(state: PagingState<Int, NewsDataMixedWithAds>): Int {
        return 0
//        return state.anchorPosition?.let { nonNullAnchorPosition ->
//            state.closestPageToPosition(nonNullAnchorPosition)?.prevKey?.plus(1) ?:
//            state.closestPageToPosition(nonNullAnchorPosition)?.nextKey?.minus(1)
//        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsDataMixedWithAds> {
        val skip = params.key ?: 0
        return try {
            val response = newsRequests.getUpdatesCoroutines(skip = skip, newsLocale = newsLocale)
            val result = parseResult(response)
            val position = response.skip ?: 0
            val nextItemsCount = response.limit ?: NEWS_COUNT_DEFAULT_VALUE
            LoadResult.Page(result, null, position + nextItemsCount)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private suspend fun parseResult(response: Updates): List<NewsDataMixedWithAds> {
        return withContext(Dispatchers.IO) {
            (response.items ?: emptyList()).toNewsMixedWithAds()
        }
    }

}