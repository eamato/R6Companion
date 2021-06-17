package eamato.funn.r6companion.adapters.recycler_view_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import eamato.funn.r6companion.R
import eamato.funn.r6companion.firebase.things.LocalizedOurTeamRemoteConfigEntity
import eamato.funn.r6companion.utils.OUR_TEAM_IMAGE_HEIGHT
import eamato.funn.r6companion.utils.OUR_TEAM_IMAGE_WIDTH
import eamato.funn.r6companion.utils.glide.GlideApp
import eamato.funn.r6companion.utils.glide.ImageResizeTransformation
import java.util.*

class OurTeamAdapter : ListAdapter<LocalizedOurTeamRemoteConfigEntity.Position, OurTeamAdapter.ViewHolder>(DIFF_ITEM_CALLBACK) {

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<LocalizedOurTeamRemoteConfigEntity.Position>() {
            override fun areItemsTheSame(oldItem: LocalizedOurTeamRemoteConfigEntity.Position, newItem: LocalizedOurTeamRemoteConfigEntity.Position): Boolean {
                return oldItem.firstName == newItem.firstName && oldItem.image == newItem.image
                        && oldItem.lastName == newItem.lastName && oldItem.positions == newItem.positions
            }

            override fun areContentsTheSame(oldItem: LocalizedOurTeamRemoteConfigEntity.Position, newItem: LocalizedOurTeamRemoteConfigEntity.Position): Boolean {
                return oldItem.firstName == newItem.firstName && oldItem.image == newItem.image
                        && oldItem.lastName == newItem.lastName && oldItem.positions == newItem.positions
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.our_team_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var iv_image: ImageView? = null
        private var tv_first_last_name: TextView? = null
        private var tv_positions: TextView? = null

        init {
            iv_image = itemView.findViewById(R.id.iv_image)
            tv_first_last_name = itemView.findViewById(R.id.tv_first_last_name)
            tv_positions = itemView.findViewById(R.id.tv_positions)
        }

        fun bind(position: LocalizedOurTeamRemoteConfigEntity.Position) {
            iv_image?.let {
                GlideApp.with(it)
                    .load(position.image)
                    .transform(ImageResizeTransformation(OUR_TEAM_IMAGE_WIDTH, OUR_TEAM_IMAGE_HEIGHT))
                    .fallback(R.drawable.img_no_image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .circleCrop()
                    .into(it)
            }

            tv_first_last_name?.text = itemView.context.getString(
                R.string.first_name_last_name_pattern,
                position.firstName?.replaceFirstChar { it.uppercaseChar() } ?: "",
                position.lastName?.replaceFirstChar { it.uppercaseChar() }  ?: ""
            )
            tv_positions?.text = position.positions?.filterNotNull()?.joinToString()
        }
    }

}