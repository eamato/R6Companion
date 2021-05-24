package eamato.funn.r6companion.utils.recyclerview

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eamato.funn.r6companion.utils.isScrollable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

abstract class AutoScroller<out L: RecyclerView.LayoutManager, T, WH: RecyclerView.ViewHolder>(
    private val recyclerView: RecyclerView?,
    protected val adapter: ListAdapter<T, WH>,
    protected val layoutManager: L
) {

    companion object {
        const val scrollToPeriod = 1000L
        const val initialDelay: Long = 0L
        const val startPosition: Int = 0
    }

    private var disposable: Disposable? = null

    private val scrollToEnd: (Long, Int) -> Int =
        fun(iteration, listSize) = (iteration % listSize + 1).toInt()

    private val scrollToStart: (Long, Int) -> Int =
        fun(iteration, listSize) = listSize - (iteration % listSize).toInt() - 1

    private val scrollToEndEndless: (Long, Int) -> Int =
        fun(iteration, listSize) = if (iteration < listSize) iteration.toInt() else listSize

    private var currentScroller = scrollToEnd

    // TODO https://www.youtube.com/watch?v=1by5J7c5Vz4&feature=youtu.be
    @SuppressLint("ClickableViewAccessibility")
    fun startAutoScrollSTETS(
        scrollToPeriod: Long = Companion.scrollToPeriod,
        initialDelay: Long = Companion.initialDelay,
        startPosition: Int = Companion.startPosition
    ) {
        if (!isScrollable())
            return
        recyclerView?.setOnTouchListener(MyOnTouchListener(::startAutoScrollSTETS, scrollToPeriod, initialDelay))
        disposable?.let { nonNullDisposable ->
            if (!nonNullDisposable.isDisposed)
                return
        }
        disposable = Flowable.interval(initialDelay, scrollToPeriod, TimeUnit.MILLISECONDS)
            .map {
                val newPosition = currentScroller.invoke(it + startPosition, adapter.itemCount)
                if (currentScroller == scrollToEnd) {
                    if (newPosition == adapter.itemCount)
                        currentScroller = scrollToStart
                } else {
                    if (newPosition == 0)
                        currentScroller = scrollToEnd
                }
                newPosition
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                recyclerView?.smoothScrollToPosition(it)
            }, {
                it.printStackTrace()
            })
    }

    // TODO https://www.youtube.com/watch?v=1by5J7c5Vz4&feature=youtu.be
    @SuppressLint("ClickableViewAccessibility")
    fun startAutoScrollSTEEndless(
        scrollToPeriod: Long = Companion.scrollToPeriod,
        initialDelay: Long = Companion.initialDelay,
        startPosition: Int = Companion.startPosition
    ) {
        if (!isScrollable())
            return
        recyclerView?.setOnTouchListener(MyOnTouchListener(::startAutoScrollSTEEndless, scrollToPeriod, initialDelay))
        disposable?.let { nonNullDisposable ->
            if (!nonNullDisposable.isDisposed)
                return
        }
        disposable = Flowable.interval(initialDelay, scrollToPeriod, TimeUnit.MILLISECONDS)
            .map {
                scrollToEndEndless.invoke(it + startPosition, adapter.itemCount)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                recyclerView?.smoothScrollToPosition(it)
            }, {
                it.printStackTrace()
            })
    }

    fun stopAutoScroll() {
        disposable?.dispose()
        disposable = null
    }

    private inner class MyOnTouchListener(
        private val doOnActionUp: (Long, Long, Int) -> Unit,
        private val fParam: Long,
        private val sParam: Long
    ): View.OnTouchListener {

        val recyclerViewScrollListener = RecyclerViewStopListener(doOnActionUp, fParam, sParam)

        private fun startAutoScrollWhenScrollFinishes() {
            if (recyclerView?.scrollState == RecyclerView.SCROLL_STATE_IDLE)
                doOnActionUp.invoke(fParam, sParam, getCurrentPosition())
            else
                recyclerView?.addOnScrollListener(recyclerViewScrollListener)
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    recyclerView?.removeOnScrollListener(recyclerViewScrollListener)
                    stopAutoScroll()
                }
                MotionEvent.ACTION_UP -> {
                    startAutoScrollWhenScrollFinishes()
                    v?.performClick()
                }
                MotionEvent.ACTION_CANCEL -> startAutoScrollWhenScrollFinishes()
            }
            return false
        }
    }

    private inner class RecyclerViewStopListener(
        private val doOnActionUp: (Long, Long, Int) -> Unit,
        private val fParam: Long,
        private val sParam: Long
    ) : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE)
                doOnActionUp.invoke(fParam, sParam, getCurrentPosition())
        }
    }

    abstract fun getCurrentPosition(): Int
    abstract fun isScrollable(): Boolean

}

class LinearAutoScroller<T, WH: RecyclerView.ViewHolder>(
    recyclerView: RecyclerView?,
    adapter: ListAdapter<T, WH>,
    layoutManager: LinearLayoutManager
) : AutoScroller<LinearLayoutManager, T, WH>(recyclerView, adapter, layoutManager) {

    override fun getCurrentPosition() = layoutManager.findLastVisibleItemPosition()

    override fun isScrollable() = layoutManager.isScrollable(adapter.itemCount)
}

class GridAutoScroller<T, WH: RecyclerView.ViewHolder>(
    recyclerView: RecyclerView?,
    adapter: ListAdapter<T, WH>,
    layoutManager: GridLayoutManager
) : AutoScroller<GridLayoutManager, T, WH>(recyclerView, adapter, layoutManager) {

    override fun getCurrentPosition() = layoutManager.findLastVisibleItemPosition()

    override fun isScrollable() = layoutManager.isScrollable(adapter.itemCount)
}
