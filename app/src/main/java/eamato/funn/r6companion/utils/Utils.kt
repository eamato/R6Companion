package eamato.funn.r6companion.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Parcelable
import android.util.DisplayMetrics
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import eamato.funn.r6companion.entities.Operators
import eamato.funn.r6companion.entities.RouletteOperator
import io.reactivex.Single
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.FileOutputStream
import kotlin.math.round

const val saveSelectionsPreferencesKey = "save_selections"

class RecyclerViewItemClickListener(
    context: Context?,
    private val recyclerView: RecyclerView,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.OnItemTouchListener {

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?) = true
            override fun onLongPress(e: MotionEvent?) {
                e?.let { nonNullMotionEvent ->
                    recyclerView.findChildViewUnder(nonNullMotionEvent.x, nonNullMotionEvent.y)
                        ?.run {
                            onItemClickListener.onItemLongClicked(
                                this,
                                recyclerView.getChildAdapterPosition(this)
                            )
                        }
                }
            }
        })

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        rv.findChildViewUnder(e.x, e.y)?.run {
            if (!gestureDetector.onTouchEvent(e))
                return false
            onItemClickListener.onItemClicked(this, rv.getChildAdapterPosition(this))
            return true
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    interface OnItemClickListener {
        fun onItemClicked(view: View, position: Int)
        fun onItemLongClicked(view: View, position: Int)
    }

}

fun RecyclerView.setOnItemClickListener(listener: RecyclerViewItemClickListener) {
    this.removeOnItemTouchListener(listener)
    this.addOnItemTouchListener(listener)
}

fun RecyclerView.setMyOnScrollListener(onScrollListener: RecyclerView.OnScrollListener) {
    this.removeOnScrollListener(onScrollListener)
    this.addOnScrollListener(onScrollListener)
}

fun LinearLayoutManager.isScrollable(dataSize: Int): Boolean {
    val firstVisibleItemPosition = this.findFirstVisibleItemPosition()
    val lastVisibleItemPosition = this.findLastVisibleItemPosition()
    return !(dataSize <= 1 || (firstVisibleItemPosition == 0 && lastVisibleItemPosition == dataSize - 1))
}

fun GridLayoutManager.isScrollable(dataSize: Int): Boolean {
    val firstVisibleItemPosition = this.findFirstVisibleItemPosition()
    val lastVisibleItemPosition = this.findLastVisibleItemPosition()
    return !(dataSize <= 1 || (firstVisibleItemPosition == 0 && lastVisibleItemPosition == dataSize - 1))
}

fun SharedPreferences.areThereSavedSelectedOperators(): Single<Boolean> {
    return Single.just(!this.getStringSet(saveSelectionsPreferencesKey, null).isNullOrEmpty())
}

fun List<Operators.Operator>.toRouletteOperators(): List<RouletteOperator> {
    return this.map(::RouletteOperator)
}

fun List<RouletteOperator>.toParcelableList(): ParcelableListOfRouletteOperators {
    return ParcelableListOfRouletteOperators(this)
}

fun Context?.isCurrentlyConnectedNetworkWIFI(): Boolean {
    if (this == null)
        return false
    val activeNetworkInfo = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        .activeNetworkInfo ?: return false
    return activeNetworkInfo.isConnected && activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
}

@Parcelize
data class ParcelableListOfRouletteOperators(val rouletteOperators: List<RouletteOperator>) :
    ArrayList<RouletteOperator>(rouletteOperators), Parcelable

interface IDoAfterTerminateGlide : RequestListener<Drawable> {
    fun doAfterTerminate()
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        doAfterTerminate()
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        doAfterTerminate()
        return false
    }
}

open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

@TargetApi(Build.VERSION_CODES.O)
fun Window.createScreenshot(displayMetrics: DisplayMetrics): Single<Bitmap> {
    return Single.create<Bitmap> {
        val bitmap = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics.heightPixels, Bitmap.Config.ARGB_8888)
        PixelCopy.request(this, bitmap,
            { copyResult ->
                if (copyResult == PixelCopy.SUCCESS)
                    it.onSuccess(bitmap)
                else
                    it.tryOnError(Throwable("Screenshot wasn't made"))
            }, Handler())
    }
}

fun View.createScreenshot(): Single<Bitmap> {
    this.isDrawingCacheEnabled = true
    val bitmap = Bitmap.createBitmap(this.drawingCache)
    this.isDrawingCacheEnabled = false
    return Single.just(bitmap)
}

fun Bitmap.toFileInInternalStorage(file: File): Single<File> {
    return Single.create<File> {
        val fileOutputStream = FileOutputStream(file)
        compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        it.onSuccess(file)
    }
}

fun createScreenshotAndGetItsUri(view: View, window: Window?, file: File, displayMetrics: DisplayMetrics): Single<File> {
    return if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        window.createScreenshot(displayMetrics).flatMap { it.toFileInInternalStorage(file) }
    } else {
        view.createScreenshot().flatMap { it.toFileInInternalStorage(file) }
    }
}

fun Int.pixelToDensityPixel(density: Float): Int {
    return round(this / density).toInt()
}