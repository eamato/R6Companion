package eamato.funn.r6companion.utils.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class ImageResizeTransformation(private val resultWidth: Int, private val resultHeight: Int)
    : BitmapTransformation() {

    private val ID = "eamato.funn.r6companion.utils.glide.ImageResizeTransformation"
    private val ID_BYTES = ID.toByteArray()

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return Bitmap.createBitmap(toTransform, 0, 0, resultWidth, resultHeight)
    }

    override fun equals(other: Any?): Boolean {
        return other is ImageResizeTransformation
    }

    override fun hashCode() = ID.hashCode()
}