package eamato.funn.r6companion.utils

import androidx.recyclerview.widget.DiffUtil
import eamato.funn.r6companion.entities.dto.UpdateDTO

data class NewsDataMixedWithAds(val newsData: UpdateDTO?, val isAd: Boolean = false) {

    companion object {
        val NEWS_DATA_DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsDataMixedWithAds>() {
            override fun areItemsTheSame(
                oldItem: NewsDataMixedWithAds,
                newItem: NewsDataMixedWithAds
            ): Boolean {
                val oldItemNewsData = oldItem.newsData
                val newItemNewsData = newItem.newsData

                return if (oldItemNewsData != null && newItemNewsData != null)
                    UpdateDTO.diffItemCallback.areItemsTheSame(oldItemNewsData, newItemNewsData)
                else
                    false
            }

            override fun areContentsTheSame(
                oldItem: NewsDataMixedWithAds,
                newItem: NewsDataMixedWithAds
            ): Boolean {
                val oldItemNewsData = oldItem.newsData
                val newItemNewsData = newItem.newsData

                return if (oldItemNewsData != null && newItemNewsData != null)
                    UpdateDTO.diffItemCallback.areContentsTheSame(oldItemNewsData, newItemNewsData)
                else
                    false
            }
        }
    }
}