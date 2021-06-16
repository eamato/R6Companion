package eamato.funn.r6companion.entities.content_view

import android.text.util.Linkify
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.content_view.abstracts.AViewContentView

data class ContentTextView(
    val text: String,
    val style: Int = R.style.AppTheme_DefaultTextViewStyle
) : AViewContentView() {

    override fun createView(parent: ViewGroup): View {
        return TextView(parent.context, null, 0, style).apply {
            text = this@ContentTextView.text
            Linkify.addLinks(this, Linkify.ALL)
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 10, 0, 0)
            }
        }
    }
}