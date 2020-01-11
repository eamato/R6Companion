package eamato.funn.r6companion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import eamato.funn.r6companion.api.requests.NewsRequests
import eamato.funn.r6companion.entities.News
import eamato.funn.r6companion.paging.NewsDataSourceFactory
import eamato.funn.r6companion.utils.NEWS_COUNT_DEFAULT_VALUE
import io.reactivex.disposables.CompositeDisposable

class HomeViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val newsDataSourceFactory = NewsDataSourceFactory(compositeDisposable, NewsRequests.getNewsRequest())
    private val newsDataSourceFactoryConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(true)
        .setPageSize(NEWS_COUNT_DEFAULT_VALUE)
        .build()

    val news: LiveData<PagedList<News.Data?>> = LivePagedListBuilder(newsDataSourceFactory, newsDataSourceFactoryConfig).build()

    val requestNewsStatus = Transformations.switchMap(newsDataSourceFactory.newsDataSource) {
        it?.requestNewsStatuses
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }

    fun refreshNews() {
        newsDataSourceFactory.refresh()
    }

    fun retry() {
        newsDataSourceFactory.retry()
    }

    fun hasDataSourceError(): Boolean {
        return newsDataSourceFactory.newsDataSource.value?.retryCompletable != null
    }

}