package eamato.funn.r6companion.utils.recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomLinearLayoutManager(context: Context?, private val scrollToPeriod: Long) : LinearLayoutManager(context) {

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean, scrollToPeriod: Long) : this(context, scrollToPeriod) {
        this.orientation = orientation
        this.reverseLayout = reverseLayout
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int, scrollToPeriod: Long) : this(context, scrollToPeriod) {
        context?.let { nonNullContext ->
            val properties = RecyclerView.LayoutManager.getProperties(nonNullContext, attrs, defStyleAttr, defStyleRes)
            orientation = properties.orientation
            reverseLayout = properties.reverseLayout
            stackFromEnd = properties.stackFromEnd
        }
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        val linearSmoothScroller = LinearInterpolationSmoothScroller(
            recyclerView?.context,
            scrollToPeriod
        ).createSmoothScroller()
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

}