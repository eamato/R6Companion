package eamato.funn.r6companion.utils.recyclerview

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration private constructor() : RecyclerView.ItemDecoration() {

    private var spanCount: Int = 1
    private var spacing: Int = 1
    private var includeEdge: Boolean = false

    constructor(spanCount: Int, spacing: Int, includeEdge: Boolean = false) : this() {
        this.spanCount = spanCount
        this.spacing = spacing
        this.includeEdge = includeEdge
    }

    constructor(
        context: Context,
        spanCount: Int,
        @DimenRes spacingResId: Int,
        includeEdge: Boolean = false
    ) : this() {
        this.spanCount = spanCount
        this.spacing =
            (context.resources.getDimension(spacingResId) / context.resources.displayMetrics.density).toInt()
        this.includeEdge = includeEdge
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
//        val position = parent.getChildAdapterPosition(view) // item position
//        val spanSizeAtPosition = parent.layoutManager
//            ?.let { it as? GridLayoutManager }
//            ?.spanSizeLookup
//            ?.getSpanSize(position)

        outRect.top = spacing
        outRect.left = spacing
        outRect.bottom = spacing
        outRect.right = spacing

//        if (spanSizeAtPosition == spanCount)
//            return

//        if (spanSizeAtPosition == 1) {
//            outRect.top = spacing
//            outRect.left = spacing
//            outRect.bottom = spacing
//            outRect.right = spacing
//            val column = position % spanCount // item column
//            if (includeEdge) {
//                outRect.left =
//                    spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
//                outRect.right =
//                    (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)
//                if (position < spanCount) { // top edge
//                    outRect.top = spacing
//                }
//                outRect.bottom = spacing // item bottom
//            } else {
//                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
//                outRect.right =
//                    spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
//                if (position >= spanCount) {
//                    outRect.top = spacing // item top
//                }
//            }
//        }
    }
}