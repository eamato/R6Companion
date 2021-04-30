package eamato.funn.r6companion.utils.glide

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import eamato.funn.r6companion.R

class ImageGetter(
    private val textView: TextView,
    private val width: Int = Target.SIZE_ORIGINAL,
    private val height: Int = Target.SIZE_ORIGINAL
) : Html.ImageGetter {

    override fun getDrawable(source: String?): Drawable? {
        val drawable = DrawableTarget()
        GlideApp
            .with(textView)
            .load(source)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .error(R.drawable.no_data_placeholder)
            .dontAnimate()
            .into(drawable)
        return drawable
    }

    private inner class DrawableTarget : Drawable(), Target<Drawable> {

        private var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }

        override fun setAlpha(alpha: Int) {
            drawable?.alpha = alpha
        }

        override fun getOpacity(): Int {
            return PixelFormat.OPAQUE
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {

        }

        override fun onLoadStarted(placeholder: Drawable?) {
            placeholder?.let { setDrawable(it) }
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            errorDrawable?.let { setDrawable(it) }
        }

        override fun getSize(cb: SizeReadyCallback) {
            cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
        }

        override fun getRequest(): Request? {
            return null
        }

        override fun onStop() {

        }

        override fun setRequest(request: Request?) {

        }

        override fun removeCallback(cb: SizeReadyCallback) {

        }

        override fun onLoadCleared(placeholder: Drawable?) {

        }

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            setDrawable(resource)
        }

        override fun onStart() {

        }

        override fun onDestroy() {

        }

        private fun setDrawable(drawable: Drawable) {
            this.drawable = drawable
            val height = width * drawable.intrinsicHeight / drawable.intrinsicWidth
            drawable.setBounds(0, 0, width, height)
            setBounds(0, 0, width, height)
            val text = textView.text
            textView.text = text
        }
    }

}