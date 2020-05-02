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
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import eamato.funn.r6companion.R
import eamato.funn.r6companion.utils.IDoAfterTerminateGlide
import eamato.funn.r6companion.utils.NewsDataMixedWithAds
import eamato.funn.r6companion.utils.glide.GlideApp
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter : PagedListAdapter<NewsDataMixedWithAds?, NewsAdapter.ViewHolder>(NewsDataMixedWithAds.NEWS_DATA_DIFF_CALLBACK) {

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
                            .load(nonNullNewsData.imageUrl)
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
                    tv_news_subtitle?.text = nonNullNewsData.subtitle
                    tv_news_date?.text = nonNullNewsData.date?.let {
                        newsDateFormat.format(it)
                    } ?: ""
                }
            }
        }

    }

    class AdItemViewHolder(itemView: View) : ViewHolder(itemView) {

        private var unav: UnifiedNativeAdView? = null
        private var ad_app_icon: ImageView? = null
        private var ad_headline: TextView? = null
        private var ad_advertiser: TextView? = null
        private var ad_stars: RatingBar? = null
        private var ad_body: TextView? = null
        private var ad_media: MediaView? = null
        private var ad_price: TextView? = null
        private var ad_store: TextView? = null
        private var ad_call_to_action: Button? = null

        private var currentNativeAd: UnifiedNativeAd? = null

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
                .forUnifiedNativeAd { ad ->
                    currentNativeAd = ad

                    unav?.let { populateAd(ad, it) }
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(p0: Int) {
                        super.onAdFailedToLoad(p0)
                    }
                })
                .withNativeAdOptions(NativeAdOptions.Builder().build())
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }

        private fun populateAd(unifiedNativeAd: UnifiedNativeAd, unifiedNativeAdView: UnifiedNativeAdView) {
            ad_headline?.text = unifiedNativeAd.headline
            ad_media?.setMediaContent(unifiedNativeAd.mediaContent)

            unifiedNativeAd.body?.let {
                ad_body?.visibility = View.VISIBLE
                ad_body?.text = it
            } ?: kotlin.run { ad_body?.visibility = View.INVISIBLE }

            unifiedNativeAd.callToAction?.let {
                ad_call_to_action?.visibility = View.VISIBLE
                ad_call_to_action?.text = it
            } ?: kotlin.run { ad_call_to_action?.visibility = View.INVISIBLE }

            unifiedNativeAd.icon?.let {
                ad_app_icon?.visibility = View.VISIBLE
                ad_app_icon?.setImageDrawable(it.drawable)
            } ?: kotlin.run { ad_app_icon?.visibility = View.INVISIBLE }

            unifiedNativeAd.price?.let {
                ad_price?.visibility = View.VISIBLE
                ad_price?.text = it
            } ?: kotlin.run { ad_price?.visibility = View.INVISIBLE }

            unifiedNativeAd.store?.let {
                ad_store?.visibility = View.VISIBLE
                ad_store?.text = it
            } ?: kotlin.run { ad_store?.visibility = View.INVISIBLE }

            unifiedNativeAd.starRating?.let {
                ad_stars?.visibility = View.VISIBLE
                ad_stars?.rating = it.toFloat()
            } ?: kotlin.run { ad_stars?.visibility = View.INVISIBLE }

            unifiedNativeAd.advertiser?.let {
                ad_advertiser?.visibility = View.VISIBLE
                ad_advertiser?.text = it
            } ?: kotlin.run { ad_advertiser?.visibility = View.INVISIBLE }

            unifiedNativeAdView.headlineView = ad_headline
            unifiedNativeAdView.bodyView = ad_body
            unifiedNativeAdView.callToActionView = ad_call_to_action
            unifiedNativeAdView.iconView = ad_app_icon
            unifiedNativeAdView.priceView = ad_price
            unifiedNativeAdView.starRatingView = ad_stars
            unifiedNativeAdView.storeView = ad_store
            unifiedNativeAdView.advertiserView = ad_advertiser
            unifiedNativeAdView.mediaView = ad_media

            unifiedNativeAdView.mediaView.setMediaContent(unifiedNativeAd.mediaContent)

            unifiedNativeAdView.setNativeAd(unifiedNativeAd)
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