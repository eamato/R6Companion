package eamato.funn.r6companion.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import eamato.funn.r6companion.api.requests.NewsRequests
import eamato.funn.r6companion.utils.*
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class NewsDataSource(
    private val compositeDisposable: CompositeDisposable,
    private val newsRequests: NewsRequests,
    private val newsLocale: String
) : PageKeyedDataSource<Int, NewsDataMixedWithAds?>() {

    private val pRequestNewsStatuses = MutableLiveData(LiveDataStatuses.IDLE)
    val requestNewsStatuses: LiveData<LiveDataStatuses> = pRequestNewsStatuses

    var retryCompletable: Completable? = null

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, NewsDataMixedWithAds?>
    ) {
        pRequestNewsStatuses.postValue(LiveDataStatuses.WAITING)
        compositeDisposable.add(
            newsRequests.getUpdates(newsLocale = newsLocale)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    callback.onResult(
                        (it.items ?: emptyList()).toNewsMixedWithAds(),
                        null,
                        it.limit
                    )
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
            newsRequests.getUpdates(params.key, newsLocale = newsLocale)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    callback.onResult(
                        (it.items ?: emptyList()).toNewsMixedWithAds(),
                        (it.skip ?: 0) + (it.limit ?: NEWS_COUNT_DEFAULT_VALUE)
                    )
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