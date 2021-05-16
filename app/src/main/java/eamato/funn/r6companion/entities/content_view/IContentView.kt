package eamato.funn.r6companion.entities.content_view

import android.content.Context
import android.view.View

interface IContentView {

    fun createView(context: Context): View

}