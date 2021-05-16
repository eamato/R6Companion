package eamato.funn.r6companion.entities.content_view

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import eamato.funn.r6companion.R
import eamato.funn.r6companion.utils.glide.GlideApp

data class ContentImageView(val link: String) : IContentView {

    override fun createView(context: Context): View {
        return ImageView(context).apply {
            GlideApp.with(this)
                .load(link)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .error(R.drawable.no_data_placeholder)
                .dontAnimate()
                .into(this)

            layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 50, 0, 0)
            }
        }
    }
}