package eamato.funn.r6companion.utils.open_pack

import android.app.ActivityManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.SurfaceHolder
import android.graphics.Matrix
import androidx.lifecycle.MutableLiveData

data class PlayRoad(
    val slides: List<Bitmap>,
    var isLooped: Boolean = false,
    var playbackStatus: MutableLiveData<PlaybackStatus> = MutableLiveData<PlaybackStatus>().apply {
        PlaybackStatus.PAUSED
    }
)

data class Playlist(
    val bottomLayerRoad: PlayRoad,
    val middleLayerRoad: PlayRoad,
    val topLayerRoad: PlayRoad,
    val duration: Long
)

data class PlaybackIteration(val bottomLayer: Bitmap?, val centerLayer: Bitmap?, val topLayer: Bitmap?)

enum class PlaybackStatus {
    PLAYING, PAUSED, STOPPED, PLAYINGFBF
}

data class Calculations(val frameCount: Int, val autoPlayThreshold: Int, val pixelsPerFrame: Int)

enum class PlaybackMode(val addition: Int) {
    STRAIGHT(1), REVERSED(-1)
}

data class MySize(val width: Int, val height: Int)

fun Canvas.getMySize(): MySize {
    return MySize(this.width, this.height)
}

fun SurfaceHolder.getCanvasMySize(): MySize {
    val canvas = this.lockCanvas()
    canvas?.let { nonNullCanvas ->
        val mySize = nonNullCanvas.getMySize()
        this.unlockCanvasAndPost(nonNullCanvas)
        return mySize
    }
    return MySize(0, 0)
}

fun BitmapFactory.Options.getFullOptimizedOptionsForMultipleImages(
    canvasSize: MySize,
    imgResId: Int,
    requiredSize: Int,
    resources: Resources,
    activityManager: ActivityManager?
): BitmapFactory.Options {
    return this
        .also { it.inJustDecodeBounds = true } // Avoid memory allocation
        .also { BitmapFactory.decodeResource(resources, imgResId, it) } // Get image sizes
        .also { it.inSampleSize = calculateInSampleSizeUsingCanvas(it, canvasSize.width, canvasSize.height) } // Get in sample size using parent size
        .also { BitmapFactory.decodeResource(resources, imgResId, it) } // Get new Image sizes (MB no need)
        .also { it.inSampleSize = calculateInSampleSizeUsingMemory(it, requiredSize, activityManager) } // Update in sample size using free memory and memory required by all images
        .also { it.inJustDecodeBounds = false } // Return default memory usage while decoding
}

fun Bitmap.getScaledBitmapToSaveSize(canvasWidth: Int, canvasHeight: Int): Bitmap {
    var result = this
    if (canvasWidth < width) {
        result = result.resizeByWidth(canvasWidth)
    }
    if (canvasHeight < height) {
        result = result.resizeByHeight(canvasHeight)
    }
    return result
}

fun Bitmap.createMatrix(canvasSize: MySize): Matrix {
    return if (canvasSize.height > canvasSize.width)
        createCenterCropMatrix(canvasSize, this)
    else
        createCenterInsideMatrix(canvasSize, this)
}

private fun createCenterCropMatrix(canvasSize: MySize, image: Bitmap): Matrix {
    val (viewWidth, viewHeight) = run { canvasSize.width to canvasSize.height }
    val (imageWidth, imageHeight) = run { image.width to image.height }
    val scale: Float
    var (dx, dy) = run { 0f to 0f }

    if (imageWidth * viewHeight > viewWidth * imageHeight) {
        scale = viewHeight.toFloat() / imageHeight.toFloat()
        dx = (viewWidth - imageWidth * scale) * 0.5f
    } else {
        scale = viewWidth.toFloat() / imageWidth.toFloat()
        dy = (viewHeight - imageHeight * scale) * 0.5f
    }

    val matrix = Matrix()
    matrix.setScale(scale, scale)
    matrix.postTranslate(Math.round(dx).toFloat(), Math.round(dy).toFloat())

    return matrix
}

private fun createCenterInsideMatrix(canvasSize: MySize, image: Bitmap): Matrix {
    val (viewWidth, viewHeight) = run { canvasSize.width to canvasSize.height }
    val (imageWidth, imageHeight) = run { image.width to image.height }
    val scale: Float
    val dx: Float
    val dy: Float

    scale = if (imageWidth <= viewWidth && imageHeight <= viewHeight) {
        1.0f
    } else {
        Math.min(
            viewWidth.toFloat() / imageWidth.toFloat(),
            viewHeight.toFloat() / imageHeight.toFloat()
        )
    }

    dx = Math.round((viewWidth - imageWidth * scale) * 0.5f).toFloat()
    dy = Math.round((viewHeight - imageHeight * scale) * 0.5f).toFloat()

    val matrix = Matrix()
    matrix.setScale(scale, scale)
    matrix.postTranslate(dx, dy)
    return matrix
}

private fun Bitmap.resizeByWidth(width:Int): Bitmap {
    val ratio: Float = this.width.toFloat() / height.toFloat()
    val height: Int = Math.round(width / ratio)

    return Bitmap.createScaledBitmap(this, width, height, false)
}

private fun Bitmap.resizeByHeight(height:Int): Bitmap {
    val ratio: Float = this.height.toFloat() / width.toFloat()
    val width: Int = Math.round(height / ratio)

    return Bitmap.createScaledBitmap(this, width, height, false)
}

private fun calculateInSampleSizeUsingCanvas(options: BitmapFactory.Options, requiredWidth: Int, requiredHeight: Int): Int {
    var inSampleSize = 1

    if (options.outWidth > requiredWidth || options.outHeight > requiredHeight) {
        val (halfWidth, halfHeight) = run { options.outWidth / 2 to options.outHeight / 2 }
        while ((halfHeight / inSampleSize) > requiredHeight && (halfWidth / inSampleSize) > requiredWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

private fun calculateInSampleSizeUsingMemory(options: BitmapFactory.Options, requiredSize: Int, activityManager: ActivityManager?): Int {
    var inSampleSize = options.inSampleSize
    val freeMemory = getFreeMemory(activityManager)
    while ((requiredSize / inSampleSize) > freeMemory) {
        inSampleSize *= 2
    }
    return inSampleSize
}

private fun getFreeMemory(activityManager: ActivityManager?): Long {
    return ActivityManager.MemoryInfo().also { activityManager?.getMemoryInfo(it) }.availMem
}