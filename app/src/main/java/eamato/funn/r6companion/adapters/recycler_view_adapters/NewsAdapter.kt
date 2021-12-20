package eamato.funn.r6companion.adapters.recycler_view_adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import eamato.funn.r6companion.R
import eamato.funn.r6companion.utils.IDoAfterTerminateGlide
import eamato.funn.r6companion.utils.NewsDataMixedWithAds
import eamato.funn.r6companion.utils.glide.GlideApp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class NewsAdapter : PagingDataAdapter<NewsDataMixedWithAds, NewsAdapter.ViewHolder>(NewsDataMixedWithAds.NEWS_DATA_DIFF_CALLBACK) {

    private val NEWS_ITEM_VIEW_TYPE = 1
    private val AD_ITEM_VIEW_TYPE = 2
    private val EMPTY_ITEM_PLACE_HOLDER = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            NEWS_ITEM_VIEW_TYPE -> NewsItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_row, parent, false))
            AD_ITEM_VIEW_TYPE -> AdItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ad_view, parent, false))
            else -> EmptyDataPlaceHolder(LayoutInflater.from(parent.context).inflate(R.layout.empty_row, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is AdItemViewHolder)
            Log.i("ADADAD", "Ad at position: $position")
        holder.bindView(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        val newsDataMixedWithAdsAtPosition = getItem(position)
        return when {
            newsDataMixedWithAdsAtPosition == null -> EMPTY_ITEM_PLACE_HOLDER
            newsDataMixedWithAdsAtPosition.isAd -> AD_ITEM_VIEW_TYPE
            else -> NEWS_ITEM_VIEW_TYPE
        }
    }

    fun getItemAtPosition(position: Int): NewsDataMixedWithAds? = getItem(position)

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindView(data: NewsDataMixedWithAds?)
    }

    class NewsItemViewHolder(itemView: View) : ViewHolder(itemView) {

        private var iv_news_image: ImageView? = null
        private var tv_news_title: TextView? = null
        private var tv_news_subtitle: TextView? = null
        private var tv_news_date: TextView? = null
        private var clpb_news_image: ContentLoadingProgressBar? = null

        private val newsDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        private val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

        init {
            iv_news_image = itemView.findViewById(R.id.iv_news_image)
            tv_news_title = itemView.findViewById(R.id.tv_news_title)
            tv_news_subtitle = itemView.findViewById(R.id.tv_news_subtitle)
            tv_news_date = itemView.findViewById(R.id.tv_news_date)
            clpb_news_image = itemView.findViewById(R.id.clpb_news_image)
        }

        override fun bindView(data: NewsDataMixedWithAds?) {
            data?.let { nonNullData ->
                nonNullData.newsData?.let { nonNullNewsData ->
                    iv_news_image?.let {
                        clpb_news_image?.show()
                        GlideApp.with(itemView.context)
                            .load(nonNullNewsData.thumbnail?.url)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .transition(DrawableTransitionOptions.withCrossFade(500))
                            .error(R.drawable.no_data_placeholder)
                            .listener(object : IDoAfterTerminateGlide {
                                override fun doAfterTerminate() {
                                    clpb_news_image?.hide()
                                }
                            })
                            .dontAnimate()
                            .into(it)
                    }
                    tv_news_title?.text = nonNullNewsData.title
                    tv_news_subtitle?.text = nonNullNewsData.abstract
//                    tv_news_date?.text = nonNullNewsData.date?.let { inputFormatter.parse(it) }?.let {
//                        newsDateFormat.format(it)
//                    } ?: ""
                }
            }
        }

    }

    class AdItemViewHolder(itemView: View) : ViewHolder(itemView) {

        private val executor = Executors.newSingleThreadExecutor()

        private var unav: NativeAdView? = null
        private var ad_app_icon: ImageView? = null
        private var ad_headline: TextView? = null
        private var ad_advertiser: TextView? = null
        private var ad_stars: RatingBar? = null
        private var ad_body: TextView? = null
        private var ad_media: MediaView? = null
        private var ad_price: TextView? = null
        private var ad_store: TextView? = null
        private var ad_call_to_action: Button? = null

        private var currentNativeAd: NativeAd? = null

        init {
            unav = itemView.findViewById(R.id.unav)
            ad_app_icon = itemView.findViewById(R.id.ad_app_icon)
            ad_headline = itemView.findViewById(R.id.ad_headline)
            ad_advertiser = itemView.findViewById(R.id.ad_advertiser)
            ad_stars = itemView.findViewById(R.id.ad_stars)
            ad_body = itemView.findViewById(R.id.ad_body)
            ad_media = itemView.findViewById(R.id.ad_media)
            ad_price = itemView.findViewById(R.id.ad_price)
            ad_store = itemView.findViewById(R.id.ad_store)
            ad_call_to_action = itemView.findViewById(R.id.ad_call_to_action)
        }

        override fun bindView(data: NewsDataMixedWithAds?) {
            hideAdViews()
            currentNativeAd?.destroy()

            val adLoader = AdLoader.Builder(itemView.context, itemView.context.getString(R.string.ad_mod_first_ad_unit))
                .forNativeAd { ad ->
                    currentNativeAd = ad

                    unav?.let { populateAd(ad, it) }
                }
                .withNativeAdOptions(NativeAdOptions.Builder().build())
                .build()

//            executor.execute {
//                adLoader.loadAd(AdRequest.Builder().build())
//            }
            adLoader.loadAd(AdRequest.Builder().build())
        }

        private fun populateAd(nativeAd: NativeAd, nativeAdView: NativeAdView) {
            ad_headline?.text = nativeAd.headline

            nativeAd.mediaContent?.let {
                ad_media?.visibility = View.VISIBLE
                ad_media?.setMediaContent(it)
            } ?: kotlin.run { ad_media?.visibility = View.INVISIBLE  }

            nativeAd.body?.let {
                ad_body?.visibility = View.VISIBLE
                ad_body?.text = it
            } ?: kotlin.run { ad_body?.visibility = View.INVISIBLE }

            nativeAd.callToAction?.let {
                ad_call_to_action?.visibility = View.VISIBLE
                ad_call_to_action?.text = it
            } ?: kotlin.run { ad_call_to_action?.visibility = View.INVISIBLE }

            nativeAd.icon?.let {
                ad_app_icon?.visibility = View.VISIBLE
                ad_app_icon?.setImageDrawable(it.drawable)
            } ?: kotlin.run { ad_app_icon?.visibility = View.INVISIBLE }

            nativeAd.price?.let {
                ad_price?.visibility = View.VISIBLE
                ad_price?.text = it
            } ?: kotlin.run { ad_price?.visibility = View.INVISIBLE }

            nativeAd.store?.let {
                ad_store?.visibility = View.VISIBLE
                ad_store?.text = it
            } ?: kotlin.run { ad_store?.visibility = View.INVISIBLE }

            nativeAd.starRating?.let {
                ad_stars?.visibility = View.VISIBLE
                ad_stars?.rating = it.toFloat()
            } ?: kotlin.run { ad_stars?.visibility = View.INVISIBLE }

            nativeAd.advertiser?.let {
                ad_advertiser?.visibility = View.VISIBLE
                ad_advertiser?.text = it
            } ?: kotlin.run { ad_advertiser?.visibility = View.INVISIBLE }

            nativeAdView.headlineView = ad_headline
            nativeAdView.bodyView = ad_body
            nativeAdView.callToActionView = ad_call_to_action
            nativeAdView.iconView = ad_app_icon
            nativeAdView.priceView = ad_price
            nativeAdView.starRatingView = ad_stars
            nativeAdView.storeView = ad_store
            nativeAdView.advertiserView = ad_advertiser
            nativeAdView.mediaView = ad_media

            nativeAd.mediaContent?.let {
                nativeAdView.mediaView?.setMediaContent(it)
            }

            nativeAdView.setNativeAd(nativeAd)
        }

        private fun hideAdViews() {
            ad_app_icon?.visibility = View.INVISIBLE
            ad_headline?.visibility = View.INVISIBLE
            ad_advertiser?.visibility = View.INVISIBLE
            ad_stars?.visibility = View.INVISIBLE
            ad_body?.visibility = View.INVISIBLE
            ad_media?.visibility = View.INVISIBLE
            ad_price?.visibility = View.INVISIBLE
            ad_store?.visibility = View.INVISIBLE
            ad_call_to_action?.visibility = View.INVISIBLE
        }
    }

    class EmptyDataPlaceHolder(itemView: View) : ViewHolder(itemView) {

        override fun bindView(data: NewsDataMixedWithAds?) {

        }

    }

}