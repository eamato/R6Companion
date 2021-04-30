package eamato.funn.r6companion.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import eamato.funn.r6companion.api.requests.NewsRequests
import eamato.funn.r6companion.utils.NewsDataMixedWithAds
import io.reactivex.disposables.CompositeDisposable

class NewsDataSourceFactory(
    private val compositeDisposable: CompositeDisposable,
    private val newsRequests: NewsRequests,
    private val newsLocale: String
) : DataSource.Factory<Int, NewsDataMixedWithAds?>() {

    private val pNewsDataSource = MutableLiveData<NewsDataSource?>(null)
    val newsDataSource: LiveData<NewsDataSource?> = pNewsDataSource

    override fun create(): DataSource<Int, NewsDataMixedWithAds?> {
        val newsDataSource = NewsDataSource(compositeDisposable, newsRequests, newsLocale)
        pNewsDataSource.postValue(newsDataSource)
        return newsDataSource
    }

    fun refresh() {
        pNewsDataSource.value?.invalidate()
    }

    fun retry() {
        pNewsDataSource.value?.retry()
    }

}