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
import eamato.funn.r6companion.entities.CompanionOperator
import eamato.funn.r6companion.entities.CompositeOperator
import eamato.funn.r6companion.entities.Operators
import eamato.funn.r6companion.utils.OPERATOR_IMAGE_HEIGHT
import eamato.funn.r6companion.utils.OPERATOR_IMAGE_WIDTH
import eamato.funn.r6companion.utils.glide.GlideApp
import eamato.funn.r6companion.utils.glide.ImageResizeTransformation
import java.util.*

class OperatorsAdapter : ListAdapter<CompanionOperator, OperatorsAdapter.ViewHolder>(DIFF_ITEM_CALLBACK) {

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<CompanionOperator>() {
            override fun areItemsTheSame(
                oldItem: CompanionOperator,
                newItem: CompanionOperator
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CompanionOperator,
                newItem: CompanionOperator
            ): Boolean {
                return oldItem.id == newItem.id
                        && oldItem.armorRating == newItem.armorRating
                        && oldItem.imgLink == newItem.imgLink
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

    fun getItemAt(position: Int): CompanionOperator {
        return getItem(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var iv_operator_image: ImageView? = null
        private var tv_operator_name: TextView? = null
        private var tv_role: TextView? = null

        init {
            iv_operator_image = itemView.findViewById(R.id.iv_operator_image)
            tv_operator_name = itemView.findViewById(R.id.tv_operator_name)
            tv_role = itemView.findViewById(R.id.tv_role)
        }

        fun bind(operator: CompanionOperator) {
            iv_operator_image?.let {
                GlideApp.with(it)
                    .load(operator.imgLink)
                    .override(OPERATOR_IMAGE_WIDTH, OPERATOR_IMAGE_HEIGHT)
                    .transform(ImageResizeTransformation(OPERATOR_IMAGE_WIDTH, OPERATOR_IMAGE_HEIGHT))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                    .placeholder(R.drawable.transparent_300)
//                    .error(R.drawable.img_no_image_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .dontAnimate()
                    .into(it)
            }
            tv_operator_name?.text = operator.name ?: ""
            tv_role?.text = operator.role?.replaceFirstChar {
                if (it.isLowerCase())
                    it.titlecase(Locale.getDefault())
                else
                    it.toString()
            } ?: ""
            tv_role?.run {
                when (operator.role) {
                    CompositeOperator.ROLE_ATTACKER -> {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attack_role, 0, 0, 0)
                    }
                    CompositeOperator.ROLE_DEFENDER -> {
                        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_defend_role, 0, 0, 0)
                    }
                    else -> setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }
            }
        }
    }

}