package eamato.funn.r6companion.utils.open_pack

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

interface MyGestureDetector : GestureDetector.OnGestureListener {

    private val flingThreshold: Int
        get() = 5000

    override fun onShowPress(e: MotionEvent) {}

    override fun onSingleTapUp(e: MotionEvent): Boolean { return true }

    override fun onLongPress(e: MotionEvent) {}

    override fun onDown(e: MotionEvent): Boolean { return true }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        val deltaX = e2.x - e1.x
        if (distanceX < 0)
            myOnScrollForXAxis(abs(deltaX), e1.x)
        else
            myOnScrollForXAxis(abs(deltaX), e1.x)
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        if (velocityX >= flingThreshold) {
            myOnSwipe(e1.x)
        }
        return true
    }

    fun myOnSwipe(startingX: Float)

    fun myOnScrollForXAxis(distanceX: Float, startingX: Float)
}