package eamato.funn.r6companion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable

abstract class RetryableViewModel : ViewModel() {

    protected val compositeDisposable = CompositeDisposable()

    protected var errorAction: Completable? = null

    protected val pIsRequestActive = MutableLiveData(false)
    val isRequestActive: LiveData<Boolean> get() = pIsRequestActive

    protected val pRequestError = MutableLiveData<Throwable?>(null)
    val requestError: LiveData<Throwable?> get() = pRequestError

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }

    protected fun retry() {
        errorAction?.let {
            compositeDisposable.add(
                it.subscribe()
            )
        }
        errorAction = null
    }

    // errorAction = Completable.fromAction { action() }

}