package eamato.funn.r6companion.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.util.DisplayMetrics
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDestination
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.Operators
import eamato.funn.r6companion.entities.ParcelableListOfRouletteOperators
import eamato.funn.r6companion.entities.RouletteOperator
import eamato.funn.r6companion.firebase.things.LocalizedRemoteConfigEntity
import eamato.funn.r6companion.utils.recyclerview.RecyclerViewItemClickListener
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import kotlin.math.round

fun RecyclerView?.setOnItemClickListener(listener: RecyclerViewItemClickListener) {
    if (this == null)
        return
    removeOnItemTouchListener(listener)
    addOnItemTouchListener(listener)
}

fun RecyclerView?.setMyOnScrollListener(onScrollListener: RecyclerView.OnScrollListener) {
    if (this == null)
        return
    removeOnScrollListener(onScrollListener)
    addOnScrollListener(onScrollListener)
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
    return Single.just(!this.getStringSet(PREFERENCE_SAVE_SELECTIONS_KEY, null).isNullOrEmpty())
}

fun SharedPreferences?.getDarkMode(): String {
    return this?.getString(PREFERENCE_DARK_MODE_KEY, PREFERENCE_DARK_MODE_DEFAULT_VALUE)
        ?: PREFERENCE_DARK_MODE_DEFAULT_VALUE
}

fun SharedPreferences?.getDarkModeIlluminationThreshold(): Int {
    return this?.getInt(PREFERENCE_ILLUMINATION_THRESHOLD_KEY, DARK_MODE_ADAPTIVE_THRESHOLD)
        ?: DARK_MODE_ADAPTIVE_THRESHOLD
}

fun SharedPreferences.getIsImageDownloadingViaMobileNetworkAllowed(): Boolean {
    return getBoolean(
        PREFERENCE_USE_MOBILE_NETWORK_FOR_IMAGE_DOWNLOAD_KEY,
        PREFERENCE_USE_MOBILE_NETWORK_FOR_IMAGE_DOWNLOAD_DEFAULT_VALUE
    )
}

fun String.setDarkMode(): Boolean {
    return when (this) {
        PREFERENCE_DARK_MODE_VALUE_OFF -> {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                true
            } else {
                false
            }
        }
        PREFERENCE_DARK_MODE_VALUE_ON -> {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                true
            } else {
                false
            }
        }
        PREFERENCE_DARK_MODE_VALUE_ADAPTIVE -> {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_UNSPECIFIED) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_UNSPECIFIED)
                true
            } else {
                false
            }
        }
        PREFERENCE_DARK_MODE_VALUE_SYSTEM_DEFAULT -> {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                true
            } else {
                false
            }
        }
        PREFERENCE_DARK_MODE_VALUE_SET_BY_BATTERY_SAVER -> {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                true
            } else {
                false
            }
        }
        else -> {
            false
        }
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
    return ContextCompat.getSystemService(this, ConnectivityManager::class.java)?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
             it.isCurrentConnectedNetworkWIFI()
        else
            it.isCurrentConnectedNetworkWIFILegacy()
    } ?: false
}

@Suppress("DEPRECATION")
fun ConnectivityManager.isCurrentConnectedNetworkWIFILegacy(): Boolean {
    val activeNetworkInfo = activeNetworkInfo ?: return false
    return activeNetworkInfo.isConnected && activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
}

@TargetApi(Build.VERSION_CODES.M)
fun ConnectivityManager.isCurrentConnectedNetworkWIFI(): Boolean {
    val network = activeNetwork
    val networkCapabilities = getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
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
            }, Handler()
        )
    }
}

@Suppress("DEPRECATION")
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

fun Int.pixelToDensityPixel(density: Float): Int {
    return round(this / density).toInt()
}

fun NavDestination.matchMenuDestination(menuItemId: Int): Boolean {
    var currentDestination: NavDestination? = this
    while (currentDestination != null && currentDestination.id != menuItemId && currentDestination.parent != null) {
        currentDestination = currentDestination.parent
    }
    return currentDestination?.id == menuItemId
}

fun <T> String.getFirebaseRemoteConfigEntity(entityClass: Class<T>): T? {
    return try {
        Gson().fromJson(this, entityClass)
    } catch (e: Exception) {
        null
    }
}

fun LocalizedRemoteConfigEntity.getText(context: Context): String {
    return if (context.getString(R.string.language) == RUSSIAN_LANGUAGE_CODE)
        ru ?: ""
    else
        en ?: ""
}

fun FragmentActivity?.getDisplayMetrics(): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    if (this == null)
        return displayMetrics
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics
}