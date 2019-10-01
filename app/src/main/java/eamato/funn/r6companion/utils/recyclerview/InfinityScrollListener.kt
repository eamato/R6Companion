package eamato.funn.r6companion.utils.recyclerview

import androidx.recyclerview.widget.RecyclerView

abstract class InfinityScrollListener : RecyclerView.OnScrollListener() {

    abstract fun prepareData()

}