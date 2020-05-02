package eamato.funn.r6companion.utils.recyclerview

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewItemClickListener(
    context: Context?,
    private val recyclerView: RecyclerView,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.OnItemTouchListener {

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?) = true
            override fun onLongPress(e: MotionEvent?) {
                e?.let { nonNullMotionEvent ->
                    recyclerView.findChildViewUnder(nonNullMotionEvent.x, nonNullMotionEvent.y)
                        ?.run {
                            val position = recyclerView.getChildAdapterPosition(this)
                            if (position != RecyclerView.NO_POSITION)
                                onItemClickListener.onItemLongClicked(this, position)
                        }
                }
            }
        })

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        rv.findChildViewUnder(e.x, e.y)?.run {
            if (!gestureDetector.onTouchEvent(e))
                return false
            val position = rv.getChildAdapterPosition(this)
            if (position != RecyclerView.NO_POSITION)
                onItemClickListener.onItemClicked(this, position)
            return false
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    interface OnItemClickListener {
        fun onItemClicked(view: View, position: Int)
        fun onItemLongClicked(view: View, position: Int)
    }

}