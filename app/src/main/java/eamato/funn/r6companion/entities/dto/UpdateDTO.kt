package eamato.funn.r6companion.entities.dto

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateDTO constructor(
    val id: String, val title: String, val subtitle: String,
    val content: String, val date: String, val type: String,
    val thumbnail: Thumbnail, var isFavourite: Boolean
) : Parcelable {

    companion object {
        val diffItemCallback = object : DiffUtil.ItemCallback<UpdateDTO>() {
            override fun areItemsTheSame(oldItem: UpdateDTO, newItem: UpdateDTO): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UpdateDTO, newItem: UpdateDTO): Boolean {
                return oldItem.title == newItem.title
                        && oldItem.content == newItem.content
                        && oldItem.subtitle == newItem.subtitle
                        && oldItem.thumbnail.url == newItem.thumbnail.url
                        && oldItem.isFavourite == newItem.isFavourite
            }
        }
    }

    @Parcelize
    data class Thumbnail(
        val url: String
    ) : Parcelable
}