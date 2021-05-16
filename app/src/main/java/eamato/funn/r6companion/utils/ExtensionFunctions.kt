package eamato.funn.r6companion.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDestination
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.*
import eamato.funn.r6companion.entities.content_view.*
import eamato.funn.r6companion.firebase.things.LocalizedRemoteConfigEntity
import eamato.funn.r6companion.utils.glide.GlideDynamicDrawableSpan
import eamato.funn.r6companion.utils.recyclerview.RecyclerViewItemClickListener
import io.reactivex.Single
import okhttp3.internal.toImmutableList
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

fun Context?.isCurrentlyConnectedToInternet(): Boolean {
    if (this == null)
        return false
    return ContextCompat.getSystemService(this, ConnectivityManager::class.java)?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            it.isCurrentConnectedToInternet()
        } else {
            it.isCurrentConnectedNetworkWIFILegacy()
        }
    } ?: false
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

@Suppress("DEPRECATION")
fun ConnectivityManager.isCurrentConnectedToInternetLegacy(): Boolean {
    val activeNetworkInfo = activeNetworkInfo ?: return false
    return activeNetworkInfo.isConnected
}

@TargetApi(Build.VERSION_CODES.M)
fun ConnectivityManager.isCurrentConnectedToInternet(): Boolean {
    val network = activeNetwork
    val networkCapabilities = getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
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

fun List<Updates.Item?>.toNewsMixedWithAds(): List<NewsDataMixedWithAds> {
    return map { NewsDataMixedWithAds(it) }.let {
        it
            .takeIf { it.size >= AD_INSERTION_COUNT }
            ?.toMutableList()
            ?.also { mutableList ->
                mutableList.insertItemAtEveryStep(NewsDataMixedWithAds(null, true), AD_INSERTION_COUNT)
            }
            ?.toImmutableList() ?: it
    }
}

fun <T> MutableList<T>.insertItemAtEveryStep(item: T, step: Int): MutableList<T> {
    var iteration = step
    val count = size / step
    for (i in 0 until count) {
        add(iteration, item)
        iteration += step
    }
    return this
}

fun String.toSpannableContent(displayMetrics: DisplayMetrics, textView: TextView): SpannableStringBuilder {
    val headerPrefix = "(?<=[^#]|^)#{3} (.*?)[\\n]".toRegex()
    val biggerHeaderPrefix = "(?<=[^#]|^)# (.*?)[\\n]".toRegex()
    val imagePrefix = "(/{2}.*?\\.(?:jpg|gif|png|jpeg))".toRegex()
    val captionPrefix2 = "(__)(.*)(__)".toRegex()
    val videoPrefix = "\\[video\\]\\((.*)\\)".toRegex()

    val imageWidth = textView.parent
        .takeIf { it is ViewGroup }
        ?.let { it as ViewGroup }
        ?.let {
            displayMetrics.widthPixels - (it.marginStart + it.marginEnd)
        } ?: displayMetrics.widthPixels

    var spannable = SpannableStringBuilder(this)

    headerPrefix.findAll(this)
        .map { it.value }
        .forEach {
            val start = indexOf(it)
            spannable.setSpan(
                RelativeSizeSpan(1.5f),
                start,
                start + it.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    biggerHeaderPrefix.findAll(this)
        .map { it.value }
        .forEach {
            val start = indexOf(it)
            spannable.setSpan(
                RelativeSizeSpan(2f),
                start,
                start + it.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    imagePrefix.findAll(this)
        .map { it.value }
        .forEach {
            val start = indexOf(it)
            spannable = spannable.insert(start, "\n")
            spannable.setSpan(
                GlideDynamicDrawableSpan(textView, imageWidth, url = "http:$it"),
                start,
                start + it.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    captionPrefix2.findAll(this)
        .map { it.value }
        .forEach {
            val start = indexOf(it)
            spannable.setSpan(
                StyleSpan(Typeface.BOLD_ITALIC),
                start,
                start + it.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    captionPrefix2.findAll(this)
        .map { it.groups }
        .forEach { _ ->
            captionPrefix2.replace(spannable)
        }

    return spannable
}

fun Regex.replace(spannableStringBuilder: SpannableStringBuilder) {
    findAll(spannableStringBuilder)
        .map { it.groups }
        .forEach {
            it.forEachIndexed { index, matchGroup ->
                if (index == 1 || index == 3) {
                    matchGroup?.value?.let { nonNullValue ->
                        val start = spannableStringBuilder.indexOf(nonNullValue)
                        spannableStringBuilder.replace(start, start + nonNullValue.length, "")
                    }
                }
            }
        }
}

fun String.contentToViewList(): List<IContentView> {
    val mainContentSplitter = "\n\n"
    val innerContentSplitter = "\n"
    val mainContent = split(mainContentSplitter)
    val contentViewList = mutableListOf<IContentView>()
    mainContent.forEach {
        if (it.contains(innerContentSplitter))
            it.split(innerContentSplitter)
                .map { innerContent ->
                    innerContent.contentToView()
                }
                .filterNotNullTo(contentViewList)
        else
            it.contentToView()
                ?.let { nonNullContentView ->
                    contentViewList.add(nonNullContentView)
                }
    }
    return contentViewList
}

fun String.contentToView(): IContentView? {
    val header1 = "(?<=[^#]|^)# (.*)".toRegex()
    val header2 = "(?<=[^#]|^)#{2} (.*)".toRegex()
    val imagePrefix = "(/{2}.*?\\.(?:jpg|gif|png|jpeg))".toRegex()
    val captionPrefix = "(?<=__)(.*?)(?=__)".toRegex()
    val italicPrefix = "(?<=\\*)(.*?)(?=\\*)".toRegex()
    val videoPrefix = "\\[video\\]\\((.*)\\)".toRegex()

    return when {
        this.startsWith("##") -> {
            header2.find(this)?.groups?.get(1)?.value?.let { nonNullValue ->
                HeaderTextView(nonNullValue, R.style.AppTheme_ContentHeader2Style)
            }
        }
        this.startsWith("#") -> {
            header1.find(this)?.groups?.get(1)?.value?.let { nonNullValue ->
                HeaderTextView(nonNullValue)
            }
        }
        this.startsWith("![") || this.startsWith("[![") -> {
            imagePrefix.find(this)?.groups?.get(1)?.value?.let { nonNullValue ->
                ContentImageView("http:$nonNullValue")
            }
        }
        this.startsWith("__") -> {
            captionPrefix.find(this)?.groups?.get(1)?.value?.let { nonNullValue ->
                ContentTextView(nonNullValue, R.style.AppTheme_ContentCaptionStyle)
            }
        }
        this.startsWith("*") -> {
            italicPrefix.find(this)?.groups?.get(1)?.value?.let { nonNullValue ->
                ContentTextView(nonNullValue, R.style.AppTheme_ContentItalicStyle)
            }
        }
        this.startsWith("[video]") -> {
            videoPrefix.find(this)?.groups?.get(1)?.value?.let { nonNullValue ->
                ContentVideoView(nonNullValue)
            }
        }
        else -> ContentTextView(this)
    }
}