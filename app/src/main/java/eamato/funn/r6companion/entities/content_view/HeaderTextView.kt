package eamato.funn.r6companion.entities.content_view

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.content_view.abstracts.AViewContentView

class HeaderTextView(
    val text: String,
    val style: Int = R.style.AppTheme_ContentHeader1Style
) : AViewContentView() {

    override fun createView(parent: ViewGroup): View {
        return TextView(parent.context, null, 0, style).apply {
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