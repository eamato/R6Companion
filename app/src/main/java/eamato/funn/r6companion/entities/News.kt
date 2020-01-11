package eamato.funn.r6companion.entities


import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName

data class News(
    @SerializedName("current_page")
    val currentPage: Int? = null,
    @SerializedName("data")
    val data: List<Data?>? = null,
    @SerializedName("first_page_url")
    val firstPageUrl: String? = null,
    @SerializedName("from")
    val from: Int? = null,
    @SerializedName("last_page")
    val lastPage: Int? = null,
    @SerializedName("last_page_url")
    val lastPageUrl: String? = null,
    @SerializedName("next_page_url")
    val nextPageUrl: String? = null,
    @SerializedName("path")
    val path: String? = null,
    @SerializedName("per_page")
    val perPage: String? = null,
    @SerializedName("prev_page_url")
    val prevPageUrl: String? = null,
    @SerializedName("to")
    val to: Int? = null,
    @SerializedName("total")
    val total: Int? = null
) {
    data class Data(
        @SerializedName("body")
        val body: String? = null,
        @SerializedName("date")
        val date: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        @SerializedName("news_id")
        val newsId: String? = null,
        @SerializedName("subtitle")
        val subtitle: String? = null,
        @SerializedName("title")
        val title: String? = null
    ) {
        companion object {
            val NEWS_DATA_DIFF_CALLBACK = object : DiffUtil.ItemCallback<Data>() {
                override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
                    return oldItem.newsId == newItem.newsId
                }

                override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
                    return oldItem.body == newItem.body && oldItem.date == oldItem.date
                            && oldItem.imageUrl == newItem.imageUrl
                            && oldItem.subtitle == newItem.subtitle
                            && oldItem.title == newItem.title
                }
            }
        }
    }
}