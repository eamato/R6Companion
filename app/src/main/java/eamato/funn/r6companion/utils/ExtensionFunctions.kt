package eamato.funn.r6companion.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.util.DisplayMetrics
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.*
import eamato.funn.r6companion.entities.content_view.*
import eamato.funn.r6companion.entities.content_view.abstracts.ContentView
import eamato.funn.r6companion.entities.dto.UpdateDTO
import eamato.funn.r6companion.firebase.things.LocalizedRemoteConfigEntity
import eamato.funn.r6companion.utils.recyclerview.RecyclerViewItemClickListener
import io.reactivex.Single
import okhttp3.internal.toImmutableList
import java.io.File
import java.io.FileOutputStream

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

fun SharedPreferences.saveFavouriteUpdate(updateDTO: UpdateDTO) {
    getFavouriteUpdates().run {
        forEach { update ->
            if (update.id == updateDTO.id)
                return
        }
        toMutableList()
            .apply { add(updateDTO) }
            .also { updatedList ->
                edit()
                    .putString(PREFERENCE_FAVOURITE_UPDATES_KEY, Gson().toJson(updatedList))
                    .apply()
            }
    }
}

fun SharedPreferences.removeFavouriteUpdate(updateDTO: UpdateDTO) {
    getFavouriteUpdates().run {
        toMutableList()
            .apply {
                val iterator = iterator()
                while (iterator.hasNext()) {
                    if (iterator.next().id == updateDTO.id)
                        iterator.remove()
                }
            }
            .also { updatedList ->
                edit()
                    .putString(PREFERENCE_FAVOURITE_UPDATES_KEY, Gson().toJson(updatedList))
                    .apply()
            }
    }
}

fun SharedPreferences.getFavouriteUpdates(): List<UpdateDTO> {
    return getString(PREFERENCE_FAVOURITE_UPDATES_KEY, null)?.let {
        try {
            val favouriteUpdatesListType = object : TypeToken<List<UpdateDTO>?>() {}.type
            Gson().fromJson<List<UpdateDTO>?>(it, favouriteUpdatesListType) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
        ?: emptyList()
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

fun List<Operators.Operator>?.toCompanionOperator(): List<CompanionOperator> {
    if (this == null)
        return emptyList()

    return this.map { operator ->
        CompanionOperator(
            id = operator.id,
            imgLink = operator.imgLink,
            wideImgLink = operator.wideImgLink,
            name = operator.name,
            operatorIconLink = operator.operatorIconLink,
            armorRating = operator.armorRating,
            equipment = CompanionOperator.Equipment(
                devices = operator.equipment?.devices?.map { device ->
                    CompanionOperator.Equipment.Device(
                        iconLink = device?.iconLink,
                        name = device?.name
                    )
                },
                primaries = operator.equipment?.primaries?.map { primary ->
                    CompanionOperator.Equipment.Primary(
                        iconLink = primary?.iconLink,
                        name = primary?.name,
                        typeText = primary?.typeText
                    )
                },
                secondaries = operator.equipment?.secondaries?.map { secondary ->
                    CompanionOperator.Equipment.Secondary(
                        iconLink = secondary?.iconLink,
                        name = secondary?.name,
                        typeText = secondary?.typeText
                    )
                },
                skill = CompanionOperator.Equipment.Skill(
                    iconLink = operator.equipment?.skill?.iconLink,
                    name = operator.equipment?.skill?.name
                )
            ),
            speedRating = operator.speedRating,
            squad = CompanionOperator.Squad(
                iconLink = operator.squad?.iconLink,
                name = operator.squad?.name
            ),
            role = operator.role
        )
    }
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

@TargetApi(Build.VERSION_CODES.M)
fun ConnectivityManager.isCurrentConnectedToInternet(): Boolean {
    val network = activeNetwork
    val networkCapabilities = getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(
        NetworkCapabilities.TRANSPORT_ETHERNET
    )
}

@TargetApi(Build.VERSION_CODES.O)
fun Window.createScreenshot(displayMetrics: DisplayMetrics): Single<Bitmap> {
    return Single.create {
        val bitmap = Bitmap.createBitmap(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            Bitmap.Config.ARGB_8888
        )
        PixelCopy.request(
            this, bitmap,
            { copyResult ->
                if (copyResult == PixelCopy.SUCCESS)
                    it.onSuccess(bitmap)
                else
                    it.tryOnError(Throwable("Screenshot wasn't made"))
            }, Handler(Looper.getMainLooper())
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
    return Single.create {
        val fileOutputStream = FileOutputStream(file)
        compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
        it.onSuccess(file)
    }
}

fun NavDestination.matchMenuDestination(menuItemId: Int): Boolean {
    var currentDestination: NavDestination? = this
    while (currentDestination != null && currentDestination.id != menuItemId && currentDestination.parent != null) {
        currentDestination = currentDestination.parent
    }
//    return this.hierarchy.any { it.id == menuItemId }
//    if (currentDestination?.id == parent?.id)
//        return true

    return currentDestination?.id == menuItemId
}

fun <T> String.getFirebaseRemoteConfigEntity(entityClass: Class<T>): T? {
    return try {
        Gson().fromJson(this, entityClass)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun LocalizedRemoteConfigEntity.getText(context: Context): String {
    return if (context.getString(R.string.lang) == RUSSIAN_LANGUAGE_CODE)
        ru ?: ""
    else
        en ?: ""
}

fun List<Updates.Item?>.toNewsMixedWithAds(pref: SharedPreferences): List<NewsDataMixedWithAds> {
    val favPref = pref.getFavouriteUpdates()
    return map {
        val id = it?.id ?: return@map null
        val title = it.title ?: return@map null
        val subtitle = it.abstract ?: ""
        val content = it.content ?: return@map null
        val date = it.date ?: return@map null
        val type = it.type ?: return@map null
        val thumbnail = it.thumbnail?.url?.run {
            UpdateDTO.Thumbnail(this)
        } ?: return@map null
        val isFavourite = favPref.find { savedUpdate ->
            savedUpdate.id == id
        }
            ?.let { true }
            ?: false

        val updateDTO = UpdateDTO(id, title, subtitle, content, date, type, thumbnail, isFavourite)

        NewsDataMixedWithAds(updateDTO)
    }.let {
        it
            .takeIf { it.size >= AD_INSERTION_COUNT }
            ?.toMutableList()
            ?.also { mutableList ->
                mutableList.insertItemAtEveryStep(
                    NewsDataMixedWithAds(null, true),
                    AD_INSERTION_COUNT
                )
            }
            ?.filterNotNull()
            ?.toImmutableList()
            ?: it.filterNotNull()
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

fun String.contentToViewList(): List<ContentView> {
    val mainContentSplitter = "\n\n"
    val innerContentSplitter = "\n"
    val mainContent = split(mainContentSplitter)
    val contentViewList = mutableListOf<ContentView>()
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

fun String.contentToView(): ContentView? {
    val header1 = "(?<=[^#]|^)# (.*)".toRegex()
    val header2 = "(?<=[^#]|^)#{2} (.*)".toRegex()
    val imagePrefix = "(/{2}.*?\\.(?:jpg|gif|png|jpeg))".toRegex()
    val captionPrefix = "(?<=__)(.*?)(?=__)".toRegex()
    val italicPrefix = "(?<=\\*)(.*?)(?=\\*)".toRegex()
    val videoPrefix = "\\[video]\\((.*)\\)".toRegex()

    val text = replace("<br>", "\n")

    return when {
        text.startsWith("##") -> {
            header2.find(text)?.groups?.get(1)?.value?.let { nonNullValue ->
                HeaderTextView(nonNullValue, R.style.AppTheme_ContentHeader2Style)
            }
        }

        text.startsWith("#") -> {
            header1.find(text)?.groups?.get(1)?.value?.let { nonNullValue ->
                HeaderTextView(nonNullValue)
            }
        }

        text.startsWith("![") || this.startsWith("[![") -> {
            imagePrefix.find(text)?.groups?.get(1)?.value?.let { nonNullValue ->
                ContentImageView("http:$nonNullValue")
            }
        }

        text.startsWith("__") -> {
            captionPrefix.find(text)?.groups?.get(1)?.value?.let { nonNullValue ->
                ContentTextView(nonNullValue, R.style.AppTheme_ContentCaptionStyle)
            }
        }

        text.startsWith("*") -> {
            italicPrefix.find(text)?.groups?.get(1)?.value?.let { nonNullValue ->
                ContentTextView(nonNullValue, R.style.AppTheme_ContentItalicStyle)
            }
        }

        text.startsWith("[video]") -> {
            videoPrefix.find(text)?.groups?.get(1)?.value?.let { nonNullValue ->
                ContentVideoView(nonNullValue)
            }
        }

        else -> ContentTextView(text)
    }
}

fun ArrayList<R6StatsOperators.R6StatsOperatorsItem>.toCompositeOperators(operators: Operators): List<CompositeOperator> {
    return filter { it.role != CompositeOperator.ROLE_RECRUIT }
        .map {
            val innerOperator = operators.operators?.filterNotNull()?.find { innerOperator ->
                innerOperator.id == it.id
            }
            CompositeOperator(
                it.armorRating, it.id, it.name, it.role, it.speedRating, innerOperator?.imgLink,
                it.ctu?.name
            )
        }
}

@SuppressLint("DiscouragedApi")
fun Context.getStringResourceByName(name: String): String {
    return try {
        val resId = resources.getIdentifier(name, "string", packageName)
        getString(resId)
    } catch (e: Exception) {
        e.printStackTrace()
        name
    }
}