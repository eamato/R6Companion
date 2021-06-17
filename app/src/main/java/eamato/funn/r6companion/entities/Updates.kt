package eamato.funn.r6companion.entities

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

data class Updates(
    @SerializedName("categoriesFilter")
    val categoriesFilter: String?,
    @SerializedName("items")
    val items: List<Item?>?,
    @SerializedName("limit")
    val limit: Int?,
    @SerializedName("mediaFilter")
    val mediaFilter: String?,
    @SerializedName("placementFilter")
    val placementFilter: String?,
    @SerializedName("skip")
    val skip: Int?,
    @SerializedName("startIndex")
    val startIndex: Int?,
    @SerializedName("tags")
    val tags: String?,
    @SerializedName("total")
    val total: Int?
) {
    @Parcelize
    data class Item(
        @SerializedName("abstract")
        val `abstract`: String?,
        @SerializedName("authors")
        val authors: @RawValue Any?,
        @SerializedName("baseUrl")
        val baseUrl: String?,
        @SerializedName("button")
        val button: Button?,
        @SerializedName("categories")
        val categories: List<String?>?,
        @SerializedName("content")
        val content: String?,
        @SerializedName("date")
        val date: String?,
        @SerializedName("featuredThumbnail")
        val featuredThumbnail: FeaturedThumbnail?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("placement")
        val placement: @RawValue Any?,
        @SerializedName("readTime")
        val readTime: String?,
        @SerializedName("tag")
        val tag: String?,
        @SerializedName("thumbnail")
        val thumbnail: Thumbnail?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("trackingPageValue")
        val trackingPageValue: String?,
        @SerializedName("type")
        val type: String?
    ): Parcelable {
        @Parcelize
        data class Button(
            @SerializedName("buttonType")
            val buttonType: String?,
            @SerializedName("buttonUrl")
            val buttonUrl: String?,
            @SerializedName("commonTranslationId")
            val commonTranslationId: String?,
            @SerializedName("trackingCategoryValue")
            val trackingCategoryValue: String?,
            @SerializedName("trackingValue")
            val trackingValue: String?
        ): Parcelable

        @Parcelize
        data class FeaturedThumbnail(
            @SerializedName("description")
            val description: @RawValue Any?,
            @SerializedName("url")
            val url: @RawValue Any?
        ): Parcelable

        @Parcelize
        data class Thumbnail(
            @SerializedName("description")
            val description: @RawValue Any?,
            @SerializedName("url")
            val url: String?
        ): Parcelable

        companion object {
            val UPDATES_ITEM_DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Updates.Item>() {
                override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return oldItem.id == newItem.id && oldItem.title == newItem.title &&
                            oldItem.content == newItem.content && oldItem.date == newItem.date &&
                            oldItem.type == newItem.type && oldItem.abstract == newItem.abstract
                }
            }
        }
    }
}