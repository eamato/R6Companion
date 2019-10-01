package eamato.funn.r6companion.utils.recyclerview

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller

class LinearInterpolationSmoothScroller(private val context: Context?, private val scrollToPeriod: Long) {

    private val scrollToPeriodMultiplier = 2

    inner class MyLinearSmoothScroller : LinearSmoothScroller(context) {
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
            return displayMetrics?.let { nonNullDisplayMetrics ->
                (scrollToPeriod * scrollToPeriodMultiplier / nonNullDisplayMetrics.densityDpi).toFloat()
            } ?: super.calculateSpeedPerPixel(displayMetrics)
        }

        override fun calculateTimeForDeceleration(dx: Int) = 0
    }

    fun createSmoothScroller() = MyLinearSmoothScroller()

}