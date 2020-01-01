package eamato.funn.r6companion.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Parcelable
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import eamato.funn.r6companion.entities.Operators
import eamato.funn.r6companion.entities.RouletteOperator
import eamato.funn.r6companion.utils.recyclerview.RecyclerViewItemClickListener
import io.reactivex.Single
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.FileOutputStream
import kotlin.math.round

const val saveSelectionsPreferencesKey = "save_selections"

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

fun SharedPreferences?.getDarkMode(): String {
    return this?.getString(PREFERENCE_DARK_MODE_KEY, PREFERENCE_DARK_MODE_DEFAULT_VALUE)
        ?: PREFERENCE_DARK_MODE_DEFAULT_VALUE
}

fun SharedPreferences?.getDarkModeIlluminationThreshold(): Int {
    return this?.getInt(PREFERENCE_ILLUMINATION_THRESHOLD_KEY, DARK_MODE_ADAPTIVE_THRESHOLD)
        ?: DARK_MODE_ADAPTIVE_THRESHOLD
}

fun String.setDarkMode() {
    when (this) {
        PREFERENCE_DARK_MODE_VALUE_OFF -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        PREFERENCE_DARK_MODE_VALUE_ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        PREFERENCE_DARK_MODE_VALUE_ADAPTIVE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_UNSPECIFIED)
    }
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
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    isCurrentConnectedNetworkWIFI(connectivityManager)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        isCurrentConnectedNetworkWIFI(connectivityManager)
    else
        isCurrentConnectedNetworkWIFILegacy(connectivityManager)
}

@Suppress("DEPRECATION")
private fun isCurrentConnectedNetworkWIFILegacy(connectivityManager: ConnectivityManager): Boolean {
    val activeNetworkInfo = connectivityManager.activeNetworkInfo ?: return false
    return activeNetworkInfo.isConnected && activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
}

@TargetApi(Build.VERSION_CODES.M)
private fun isCurrentConnectedNetworkWIFI(connectivityManager: ConnectivityManager): Boolean {
    val network = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
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