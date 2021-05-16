package eamato.funn.r6companion.entities.content_view

import android.content.Context
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import eamato.funn.r6companion.R

data class ContentTextView(val text: String, val style: Int = R.style.AppTheme_DefaultTextViewStyle) : IContentView {

    override fun createView(context: Context): View {
        return TextView(context, null, 0, style).apply {
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