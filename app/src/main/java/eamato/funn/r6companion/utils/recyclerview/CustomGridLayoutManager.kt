package eamato.funn.r6companion.utils.recyclerview

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomGridLayoutManager(context: Context?, spanCount: Int, private val scrollToPeriod: Long)
    : GridLayoutManager(context, spanCount) {

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        val linearSmoothScroller = LinearInterpolationSmoothScroller(
            recyclerView?.context,
            scrollToPeriod
        ).createSmoothScroller()
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

}