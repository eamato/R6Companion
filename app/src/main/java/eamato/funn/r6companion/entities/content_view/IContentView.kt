package eamato.funn.r6companion.entities.content_view

import android.view.View
import android.view.ViewGroup

interface IContentView {

    fun createView(parent: ViewGroup): View

}