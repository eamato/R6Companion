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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.CompositeOperator
import eamato.funn.r6companion.utils.OPERATOR_IMAGE_HEIGHT
import eamato.funn.r6companion.utils.OPERATOR_IMAGE_WIDTH
import eamato.funn.r6companion.utils.glide.GlideApp
import eamato.funn.r6companion.utils.glide.ImageResizeTransformation
import java.util.*

class OperatorsAdapter : ListAdapter<CompositeOperator, OperatorsAdapter.ViewHolder>(DIFF_ITEM_CALLBACK) {

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<CompositeOperator>() {
            override fun areItemsTheSame(
                oldItem: CompositeOperator,
                newItem: CompositeOperator
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CompositeOperator,
                newItem: CompositeOperator
            ): Boolean {
                return oldItem.id == newItem.id
                        && oldItem.armorRating == newItem.armorRating
                        && oldItem.imageUrl == newItem.imageUrl
                        && oldItem.name == newItem.name
                        && oldItem.role == newItem.role
                        && oldItem.speedRating == newItem.speedRating
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.operator_row, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var iv_operator_image: ImageView? = null
        private var tv_operator_name: TextView? = null
        private var tv_ctu_name: TextView? = null
        private var iv_role_icon: ImageView? = null
        private var tv_role: TextView? = null
        private var v_armor_1: View? = null
        private var v_armor_2: View? = null
        private var v_armor_3: View? = null
        private var v_speed_1: View? = null
        private var v_speed_2: View? = null
        private var v_speed_3: View? = null

        init {
            iv_operator_image = itemView.findViewById(R.id.iv_operator_image)
            tv_operator_name = itemView.findViewById(R.id.tv_operator_name)
            tv_ctu_name = itemView.findViewById(R.id.tv_ctu_name)
            iv_role_icon = itemView.findViewById(R.id.iv_role_icon)
            tv_role = itemView.findViewById(R.id.tv_role)
            v_armor_1 = itemView.findViewById(R.id.v_armor_1)
            v_armor_2 = itemView.findViewById(R.id.v_armor_2)
            v_armor_3 = itemView.findViewById(R.id.v_armor_3)
            v_speed_1 = itemView.findViewById(R.id.v_speed_1)
            v_speed_2 = itemView.findViewById(R.id.v_speed_2)
            v_speed_3 = itemView.findViewById(R.id.v_speed_3)
        }

        fun bind(compositeOperator: CompositeOperator) {
            iv_operator_image?.let {
                GlideApp.with(it)
                    .load(compositeOperator.imageUrl)
                    .override(OPERATOR_IMAGE_WIDTH, OPERATOR_IMAGE_HEIGHT)
                    .transform(ImageResizeTransformation(OPERATOR_IMAGE_WIDTH, OPERATOR_IMAGE_HEIGHT))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                    .placeholder(R.drawable.transparent_300)
//                    .error(R.drawable.img_no_image_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .dontAnimate()
                    .into(it)
            }
            tv_operator_name?.text = compositeOperator.name ?: ""
            tv_ctu_name?.text = compositeOperator.ctuName ?: ""
            iv_role_icon?.let {
                when (compositeOperator.role) {
                    CompositeOperator.ROLE_ATTACKER -> {
                        it.setImageResource(R.drawable.ic_attack_role)
                    }
                    CompositeOperator.ROLE_DEFENDER -> {
                        it.setImageResource(R.drawable.ic_defend_role)
                    }
                    else -> it.setImageDrawable(null)
                }
            }
            tv_role?.text = compositeOperator.role?.replaceFirstChar {
                if (it.isLowerCase())
                    it.titlecase(Locale.getDefault())
                else
                    it.toString()
            } ?: ""

            v_armor_1?.isEnabled = false
            v_armor_2?.isEnabled = false
            v_armor_3?.isEnabled = false

            v_speed_1?.isEnabled = false
            v_speed_2?.isEnabled = false
            v_speed_3?.isEnabled = false

            when (compositeOperator.armorRating) {
                1 -> v_armor_1?.isEnabled = true
                2 -> {
                    v_armor_1?.isEnabled = true
                    v_armor_2?.isEnabled = true
                }
                3 -> {
                    v_armor_1?.isEnabled = true
                    v_armor_2?.isEnabled = true
                    v_armor_3?.isEnabled = true
                }
            }

            when (compositeOperator.speedRating) {
                1 -> v_speed_1?.isEnabled = true
                2 -> {
                    v_speed_1?.isEnabled = true
                    v_speed_2?.isEnabled = true
                }
                3 -> {
                    v_speed_1?.isEnabled = true
                    v_speed_2?.isEnabled = true
                    v_speed_3?.isEnabled = true
                }
            }
        }
    }

}