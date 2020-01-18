package eamato.funn.r6companion.adapters.recycler_view_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
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

        private var iv_ad_icon: ImageView? = null
        private var tv_ad_headline: TextView? = null
        private var tv_ad_body: TextView? = null
        private var tv_ad_call_to_action: TextView? = null

        init {
            iv_ad_icon = itemView.findViewById(R.id.iv_ad_icon)
            tv_ad_headline = itemView.findViewById(R.id.tv_ad_headline)
            tv_ad_body = itemView.findViewById(R.id.tv_ad_body)
            tv_ad_call_to_action = itemView.findViewById(R.id.tv_ad_call_to_action)
        }

        override fun bindView(data: NewsDataMixedWithAds?) {
            val adLoader = AdLoader.Builder(itemView.context, itemView.context.getString(R.string.ad_mod_first_ad_unit))
                .forUnifiedNativeAd { ad ->
                    ad.icon?.let { nonNullAdIcon ->
                        iv_ad_icon?.setImageDrawable(nonNullAdIcon.drawable)
                    }
                    tv_ad_headline?.text = ad.headline
                    tv_ad_body?.text = ad.body
                    tv_ad_call_to_action?.text = ad.callToAction
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(p0: Int) {
                        super.onAdFailedToLoad(p0)
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                    }
                })
                .withNativeAdOptions(NativeAdOptions.Builder().build())
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    class EmptyDataPlaceHolder(itemView: View) : ViewHolder(itemView) {

        override fun bindView(data: NewsDataMixedWithAds?) {

        }

    }

}