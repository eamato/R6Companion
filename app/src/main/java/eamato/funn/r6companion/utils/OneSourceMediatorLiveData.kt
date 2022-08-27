package eamato.funn.r6companion.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

class OneSourceMediatorLiveData<T> : MediatorLiveData<T>() {

    private var currentSource: LiveData<*>? = null

    override fun <S : Any?> addSource(source: LiveData<S>, onChanged: Observer<in S>) {
        currentSource?.run {
            removeSource(this)
        }

        currentSource = source
        super.addSource(source, onChanged)
    }

    fun <S : Any?> setSource(source: LiveData<S>, onChanged: Observer<in S>) {
        addSource(source, onChanged)
    }
}