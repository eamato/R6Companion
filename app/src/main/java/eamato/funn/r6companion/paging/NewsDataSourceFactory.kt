package eamato.funn.r6companion.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import eamato.funn.r6companion.api.requests.NewsRequests
import eamato.funn.r6companion.entities.News
import io.reactivex.disposables.CompositeDisposable

class NewsDataSourceFactory(
    private val compositeDisposable: CompositeDisposable,
    private val newsRequests: NewsRequests
) : DataSource.Factory<Int, News.Data?>() {

    private val pNewsDataSource = MutableLiveData<NewsDataSource?>(null)
    val newsDataSource: LiveData<NewsDataSource?> = pNewsDataSource

    override fun create(): DataSource<Int, News.Data?> {
        val newsDataSource = NewsDataSource(compositeDisposable, newsRequests)
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