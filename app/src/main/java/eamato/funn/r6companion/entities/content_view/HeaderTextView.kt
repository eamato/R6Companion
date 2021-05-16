package eamato.funn.r6companion.entities.content_view

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import eamato.funn.r6companion.R

class HeaderTextView(val text: String, val style: Int = R.style.AppTheme_ContentHeader1Style) : IContentView {

    override fun createView(context: Context): View {
        return TextView(context, null, 0, style).apply {
            text = this@HeaderTextView.text
            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 50, 0, 0)
            }
        }
    }
}