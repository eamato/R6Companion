package eamato.funn.r6companion.utils.okhttp

import android.content.Context
import eamato.funn.r6companion.BuildConfig
import eamato.funn.r6companion.utils.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

fun getWifiOnlyRequestInterceptor(context: Context) = object : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (context.isCurrentlyConnectedNetworkWIFI())
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
}

val loginInterceptor = HttpLoggingInterceptor()
    .also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }

val requestInterceptor = object : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()

        val url = oldRequest
            .url
            .newBuilder()
//            .addQueryParameter()
            .build()

        val newRequest = oldRequest
            .newBuilder()
            .url(url)
            .addHeader("Test-Header", "Test")
            .build()

        return chain.proceed(newRequest)
    }
}

val defaultOkHttpClient = OkHttpClient
    .Builder()
    .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
    .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
    .writeTimeout(WRITE_TIME_OUT, TimeUnit.MILLISECONDS)
    .addInterceptor(requestInterceptor)
    .also {
        if (BuildConfig.DEBUG)
            it.addInterceptor(loginInterceptor)
    }
    .build()

fun getImageOkHttpClient(context: Context) = defaultOkHttpClient
    .newBuilder()
    .addInterceptor(getWifiOnlyRequestInterceptor(context))
    .build()