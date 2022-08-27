package eamato.funn.r6companion.entities

import com.google.gson.annotations.SerializedName
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
    @SerializedName("skip")
    val skip: Int?,
    @SerializedName("startIndex")
    val startIndex: Int?,
    @SerializedName("total")
    val total: Int?
) {

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
    ) {
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
        )

        data class FeaturedThumbnail(
            @SerializedName("description")
            val description: @RawValue Any?,
            @SerializedName("url")
            val url: @RawValue Any?
        )

        data class Thumbnail(
            @SerializedName("description")
            val description: @RawValue Any?,
            @SerializedName("url")
            val url: String?
        )
    }
}