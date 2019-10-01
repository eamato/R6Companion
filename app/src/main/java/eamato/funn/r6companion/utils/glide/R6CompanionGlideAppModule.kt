package eamato.funn.r6companion.utils.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.utils.isCurrentlyConnectedNetworkWIFI
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

@GlideModule
class R6CompanionGlideAppModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                if (context.isCurrentlyConnectedNetworkWIFI())
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
            .build()

        val factory = OkHttpUrlLoader.Factory(client)

        glide.registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }

}