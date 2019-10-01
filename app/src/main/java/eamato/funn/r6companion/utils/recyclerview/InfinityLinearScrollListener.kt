package eamato.funn.r6companion.utils.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eamato.funn.r6companion.utils.isScrollable

class InfinityLinearScrollListener<T>(
    private val layoutManager: LinearLayoutManager,
    private val data: MutableList<T>
) : InfinityScrollListener() {

    private val defaultDataSize = data.size
    private val defaultData = ArrayList(data)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!layoutManager.isScrollable(data.size))
            return

        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        val dxy = when (layoutManager.orientation) {
            RecyclerView.HORIZONTAL -> dx
            RecyclerView.VERTICAL -> dy
            else -> return
        }

        if (dxy > 0) {
            if (lastVisibleItemPosition == data.size - 1) {
                recyclerView.post {
                    if (data.size / defaultDataSize == 3) {
                        // time to delete head
                        for (i in 1..defaultDataSize) {
                            data.removeAt(0)
                        }
                        recyclerView.adapter?.notifyItemRangeRemoved(0, defaultDataSize)
                    }
                    val deletingIndex = data.size + 1
                    data.addAll(defaultData)
                    recyclerView.adapter?.notifyItemRangeInserted(deletingIndex, defaultDataSize)
                }
            }
        } else {
            if (firstVisibleItemPosition == 0) {
                recyclerView.post {
                    if (data.size / defaultDataSize == 3) {
                        // time to delete tail
                        val deletingIndex = data.size - defaultDataSize
                        for (i in 1..defaultDataSize) {
                            data.removeAt(deletingIndex)
                        }
                        recyclerView.adapter?.notifyItemRangeRemoved(deletingIndex, defaultDataSize)
                    }
                    data.addAll(0, defaultData)
                    recyclerView.adapter?.notifyItemRangeInserted(0, defaultDataSize)
                }
            }
        }
    }

    override fun prepareData() {
        if (layoutManager.isScrollable(data.size)) {
            data.addAll(defaultData)
            data.addAll(0, defaultData)
            layoutManager.scrollToPosition(defaultDataSize)
        }
    }

}