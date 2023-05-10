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
    @SerializedName("total")
    val total: Int?
) {
    data class Item(
        @SerializedName("abstract")
        val `abstract`: String?,
        @SerializedName("content")
        val content: String?,
        @SerializedName("date")
        val date: String?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("thumbnail")
        val thumbnail: Thumbnail?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("type")
        val type: String?
    ) {
        data class Thumbnail(
            @SerializedName("description")
            val description: String?,
            @SerializedName("url")
            val url: String?
        )
    }
}