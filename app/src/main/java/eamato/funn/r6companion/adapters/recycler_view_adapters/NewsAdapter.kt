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
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.News
import eamato.funn.r6companion.utils.IDoAfterTerminateGlide
import eamato.funn.r6companion.utils.glide.GlideApp

class NewsAdapter : PagedListAdapter<News.Data, NewsAdapter.ViewHolder>(News.Data.NEWS_DATA_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.news_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bingView(getItem(position))
    }

    fun getItemAtPosition(position: Int): News.Data? = getItem(position)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var iv_news_image: ImageView? = null
        private var tv_news_title: TextView? = null
        private var tv_news_subtitle: TextView? = null
        private var clpb_news_image: ContentLoadingProgressBar? = null

        init {
            iv_news_image = itemView.findViewById(R.id.iv_news_image)
            tv_news_title = itemView.findViewById(R.id.tv_news_title)
            tv_news_subtitle = itemView.findViewById(R.id.tv_news_subtitle)
            clpb_news_image = itemView.findViewById(R.id.clpb_news_image)
        }

        fun bingView(data: News.Data?) {
            iv_news_image?.let {
                clpb_news_image?.show()
                GlideApp.with(itemView.context)
                    .load(data?.imageUrl)
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
            tv_news_title?.text = data?.title
            tv_news_subtitle?.text = data?.subtitle
        }

    }

}