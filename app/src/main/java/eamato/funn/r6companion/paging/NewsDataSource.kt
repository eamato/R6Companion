package eamato.funn.r6companion.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import eamato.funn.r6companion.api.requests.NewsRequests
import eamato.funn.r6companion.utils.LiveDataStatuses
import eamato.funn.r6companion.utils.NEWS_COUNT_DEFAULT_VALUE
import eamato.funn.r6companion.utils.NewsDataMixedWithAds
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.internal.toImmutableList

class NewsDataSource(
    private val compositeDisposable: CompositeDisposable,
    private val newsRequests: NewsRequests
) : PageKeyedDataSource<Int, NewsDataMixedWithAds?>() {

    private val pRequestNewsStatuses = MutableLiveData<LiveDataStatuses>(LiveDataStatuses.IDLE)
    val requestNewsStatuses: LiveData<LiveDataStatuses> = pRequestNewsStatuses

    var retryCompletable: Completable? = null

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, NewsDataMixedWithAds?>
    ) {
        pRequestNewsStatuses.postValue(LiveDataStatuses.WAITING)
        compositeDisposable.add(
            newsRequests.getNews(NEWS_COUNT_DEFAULT_VALUE, 1)
                .subscribeOn(Schedulers.io())
                .map {
                    it.data ?: emptyList()
                }
                .subscribe({
                    val result = it
                        .map { newsData -> NewsDataMixedWithAds(newsData) }
                        .toMutableList()
                        .also { res ->
                            res.add(NewsDataMixedWithAds(null, true))
                        }
                        .toImmutableList()
                    callback.onResult(result, null, 2)
                    pRequestNewsStatuses.postValue(LiveDataStatuses.DONE)
                    retryCompletable = null
                }, {
                    retryCompletable = Completable.fromAction { loadInitial(params, callback) }
                    pRequestNewsStatuses.postValue(LiveDataStatuses.ERROR)
                })
        )
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, NewsDataMixedWithAds?>
    ) {

    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, NewsDataMixedWithAds?>
    ) {
        pRequestNewsStatuses.postValue(LiveDataStatuses.WAITING)
        compositeDisposable.add(
            newsRequests.getNews(NEWS_COUNT_DEFAULT_VALUE, params.key)
                .subscribeOn(Schedulers.io())
                .map {
                    it.data ?: emptyList()
                }
                .subscribe({
                    val result = it
                        .map { newsData -> NewsDataMixedWithAds(newsData) }
                        .toMutableList()
                        .also { res -> res.add(NewsDataMixedWithAds(null, true)) }
                        .toImmutableList()
                    callback.onResult(result, params.key.inc())
                    pRequestNewsStatuses.postValue(LiveDataStatuses.DONE)
                    retryCompletable = null
                }, {
                    retryCompletable = Completable.fromAction { loadAfter(params, callback) }
                    pRequestNewsStatuses.postValue(LiveDataStatuses.ERROR)
                })
        )
    }

    fun retry() {
        retryCompletable?.let { nonNullRetryCompletable ->
            compositeDisposable.add(
                nonNullRetryCompletable.subscribeOn(Schedulers.io()).subscribe()
            )
        }
        retryCompletable = null
    }

}