package eamato.funn.r6companion.utils.okhttp

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import eamato.funn.r6companion.BuildConfig
import eamato.funn.r6companion.utils.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

fun getWifiOnlyRequestInterceptor(context: Context) = Interceptor { chain ->
    if (
        context.isCurrentlyConnectedNetworkWIFI() ||
        PreferenceManager.getDefaultSharedPreferences(context).getIsImageDownloadingViaMobileNetworkAllowed()
    )
        chain.proceed(chain.request())
    else
        chain
            .withConnectTimeout(1, TimeUnit.SECONDS)
            .proceed(
                chain
                    .request()
                    .newBuilder()
                    .url(UN_EXISTENT_HOST)
                    .build()
            )
}

val defaultLoggingInterceptor = HttpLoggingInterceptor()
    .also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }

val imageLoggingInterceptor = HttpLoggingInterceptor {
    Log.d(IMAGE_LOGGER_TAG, it)
}
    .also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }

val requestInterceptor = Interceptor { chain ->
    val oldRequest = chain.request()

    val url = oldRequest
        .url
        .newBuilder()
//            .addQueryParameter()
        .build()

    val newRequest = oldRequest
        .newBuilder()
        .url(url)
//            .addHeader("Test-Header", "Test")
        .build()

    chain.proceed(newRequest)
}

val defaultOkHttpClient = OkHttpClient
    .Builder()
    .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
    .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
    .writeTimeout(WRITE_TIME_OUT, TimeUnit.MILLISECONDS)
    .addInterceptor(requestInterceptor)
    .also {
        if (BuildConfig.DEBUG)
            it.addInterceptor(defaultLoggingInterceptor)
    }
    .build()

fun getImageOkHttpClient(context: Context) = defaultOkHttpClient
    .newBuilder()
    .addInterceptor(getWifiOnlyRequestInterceptor(context))
    .also {
        if (BuildConfig.DEBUG)
            it.addInterceptor(imageLoggingInterceptor)
    }
    .build()