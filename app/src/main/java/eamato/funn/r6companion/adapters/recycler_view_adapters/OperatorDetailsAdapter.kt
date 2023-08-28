package eamato.funn.r6companion.adapters.recycler_view_adapters

import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import eamato.funn.r6companion.databinding.OperatorDetailDividerItemRowBinding
import eamato.funn.r6companion.databinding.OperatorDetailImageItemRowBinding
import eamato.funn.r6companion.databinding.OperatorDetailLoadOutEntityItemRowBinding
import eamato.funn.r6companion.databinding.OperatorDetailStatItemRowBinding
import eamato.funn.r6companion.databinding.OperatorDetailSubtitleItemRowBinding
import eamato.funn.r6companion.databinding.OperatorDetailTextItemRowBinding
import eamato.funn.r6companion.databinding.OperatorDetailTitleItemRowBinding
import eamato.funn.r6companion.entities.OperatorDetails
import eamato.funn.r6companion.entities.OperatorDetails.Companion.VIEW_TYPE_ABILITY_ENTITY
import eamato.funn.r6companion.entities.OperatorDetails.Companion.VIEW_TYPE_DIVIDER
import eamato.funn.r6companion.entities.OperatorDetails.Companion.VIEW_TYPE_IMAGE
import eamato.funn.r6companion.entities.OperatorDetails.Companion.VIEW_TYPE_LOAD_OUT_ENTITY
import eamato.funn.r6companion.entities.OperatorDetails.Companion.VIEW_TYPE_ORGANIZATION_ENTITY
import eamato.funn.r6companion.entities.OperatorDetails.Companion.VIEW_TYPE_STAT
import eamato.funn.r6companion.entities.OperatorDetails.Companion.VIEW_TYPE_SUBTITLE
import eamato.funn.r6companion.entities.OperatorDetails.Companion.VIEW_TYPE_TEXT
import eamato.funn.r6companion.entities.OperatorDetails.Companion.VIEW_TYPE_TITLE
import eamato.funn.r6companion.utils.NEGATIVE
import eamato.funn.r6companion.utils.glide.GlideApp

class OperatorDetailsAdapter : ListAdapter<OperatorDetails, OperatorDetailsAdapter.ViewHolder>(DIFF_ITEM_CALLBACK) {

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<OperatorDetails>() {
            override fun areItemsTheSame(
                oldItem: OperatorDetails,
                newItem: OperatorDetails
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: OperatorDetails,
                newItem: OperatorDetails
            ): Boolean {
                if (oldItem is OperatorDetails.OperatorDetailsImage && newItem is OperatorDetails.OperatorDetailsImage) {
                    return oldItem.imageUrl == newItem.imageUrl
                }
                if (oldItem is OperatorDetails.OperatorDetailsTitle && newItem is OperatorDetails.OperatorDetailsTitle) {
                    return oldItem.title == newItem.title
                }
                if (oldItem is OperatorDetails.OperatorDetailsSubtitle && newItem is OperatorDetails.OperatorDetailsSubtitle) {
                    return oldItem.subtitle == newItem.subtitle
                }
                if (oldItem is OperatorDetails.OperatorDetailsText && newItem is OperatorDetails.OperatorDetailsText) {
                    return oldItem.text == newItem.text
                }
                if (oldItem is OperatorDetails.OperatorDetailsStat && newItem is OperatorDetails.OperatorDetailsStat) {
                    return oldItem.name == newItem.name && oldItem.value == newItem.value
                }
                if (oldItem is OperatorDetails.OperatorDetailsLoadOutEntity && newItem is OperatorDetails.OperatorDetailsLoadOutEntity) {
                    return oldItem.name == newItem.name && oldItem.imageUrl == newItem.imageUrl && oldItem.typeText == newItem.typeText
                }
                if (oldItem is OperatorDetails.OperatorDetailsAbilityEntity && newItem is OperatorDetails.OperatorDetailsAbilityEntity) {
                    return oldItem.name == newItem.name && oldItem.imageUrl == newItem.imageUrl
                }
                if (oldItem is OperatorDetails.OperatorDetailsDivider && newItem is OperatorDetails.OperatorDetailsDivider) {
                    return true
                }
                if (oldItem is OperatorDetails.OrganizationEntity && newItem is OperatorDetails.OrganizationEntity) {
                    return oldItem.name == newItem.name && oldItem.imageUrl == newItem.imageUrl
                }

                return false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_IMAGE -> ViewHolder.OperatorDetailImageViewHolder(
                OperatorDetailImageItemRowBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_TITLE -> ViewHolder.OperatorDetailTitleViewHolder(
                OperatorDetailTitleItemRowBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_SUBTITLE -> ViewHolder.OperatorDetailSubtitleViewHolder(
                OperatorDetailSubtitleItemRowBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_TEXT -> ViewHolder.OperatorDetailTextViewHolder(
                OperatorDetailTextItemRowBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_STAT -> ViewHolder.OperatorDetailStatViewHolder(
                OperatorDetailStatItemRowBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_LOAD_OUT_ENTITY -> ViewHolder.OperatorDetailLoadOutEntityViewHolder(
                OperatorDetailLoadOutEntityItemRowBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_ABILITY_ENTITY -> ViewHolder.OperatorDetailsAbilityEntityViewHolder(
                OperatorDetailLoadOutEntityItemRowBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_DIVIDER -> ViewHolder.OperatorDetailDividerViewHolder(
                OperatorDetailDividerItemRowBinding.inflate(inflater, parent, false)
            )
            VIEW_TYPE_ORGANIZATION_ENTITY -> ViewHolder.OrganizationViewHolder(
                OperatorDetailTextItemRowBinding.inflate(inflater, parent, false)
            )
            else -> throw Exception("Unknown viewType found ${this.javaClass.name}")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getItemViewType()
    }

    sealed class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun <T: OperatorDetails>bind(operatorDetail: T)

        class OperatorDetailImageViewHolder(
            private val binding: OperatorDetailImageItemRowBinding
        ) : ViewHolder(binding.root) {

            override fun <T : OperatorDetails> bind(operatorDetail: T) {
                if (operatorDetail !is OperatorDetails.OperatorDetailsImage) {
                    return
                }

                GlideApp.with(binding.ivOperatorImage)
                    .asDrawable()
                    .load(operatorDetail.imageUrl.asString(itemView.context))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(binding.ivOperatorImage)
            }
        }

        class OperatorDetailTitleViewHolder(
            private val binding: OperatorDetailTitleItemRowBinding
        ) : ViewHolder(binding.root) {

            override fun <T : OperatorDetails> bind(operatorDetail: T) {
                if (operatorDetail !is OperatorDetails.OperatorDetailsTitle) {
                    return
                }

                binding.tvTitle.text = operatorDetail.title.asString(itemView.context)
            }
        }

        class OperatorDetailSubtitleViewHolder(
            private val binding: OperatorDetailSubtitleItemRowBinding
        ) : ViewHolder(binding.root) {

            override fun <T : OperatorDetails> bind(operatorDetail: T) {
                if (operatorDetail !is OperatorDetails.OperatorDetailsSubtitle) {
                    return
                }

                binding.tvSubtitle.text = operatorDetail.subtitle.asString(itemView.context)
            }
        }

        class OperatorDetailTextViewHolder(
            private val binding: OperatorDetailTextItemRowBinding
        ) : ViewHolder(binding.root) {

            override fun <T : OperatorDetails> bind(operatorDetail: T) {
                if (operatorDetail !is OperatorDetails.OperatorDetailsText) {
                    return
                }

                binding.tvText.text = operatorDetail.text.asString(itemView.context)
            }
        }

        class OperatorDetailStatViewHolder(
            private val binding: OperatorDetailStatItemRowBinding
        ) : ViewHolder(binding.root) {

            override fun <T : OperatorDetails> bind(operatorDetail: T) {
                if (operatorDetail !is OperatorDetails.OperatorDetailsStat) {
                    return
                }

                binding.tvStatName.text = operatorDetail.name.asString(itemView.context)

                binding.vStat1.isEnabled = false
                binding.vStat2.isEnabled = false
                binding.vStat3.isEnabled = false

                when (operatorDetail.value) {
                    1 -> binding.vStat1.isEnabled = true
                    2 -> {
                        binding.vStat1.isEnabled = true
                        binding.vStat2.isEnabled = true
                    }
                    3 -> {
                        binding.vStat1.isEnabled = true
                        binding.vStat2.isEnabled = true
                        binding.vStat3.isEnabled = true
                    }
                }
            }
        }

        class OperatorDetailLoadOutEntityViewHolder(
            private val binding: OperatorDetailLoadOutEntityItemRowBinding
        ) : ViewHolder(binding.root) {

            override fun <T : OperatorDetails> bind(operatorDetail: T) {
                if (operatorDetail !is OperatorDetails.OperatorDetailsLoadOutEntity) {
                    return
                }

                binding.tvLoadOutName.text = operatorDetail.name.asString(itemView.context)

                GlideApp.with(binding.ivLoadOutImage)
                    .load(operatorDetail.imageUrl.asString(itemView.context))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(binding.ivLoadOutImage)

                binding.tvLoadOutType.text = operatorDetail.typeText?.asString(itemView.context)
            }
        }

        class OperatorDetailsAbilityEntityViewHolder(
            private val binding: OperatorDetailLoadOutEntityItemRowBinding
        ) : ViewHolder(binding.root) {

            override fun <T : OperatorDetails> bind(operatorDetail: T) {
                if (operatorDetail !is OperatorDetails.OperatorDetailsAbilityEntity) {
                    return
                }

                binding.tvLoadOutName.text = operatorDetail.name.asString(itemView.context)

                GlideApp.with(binding.ivLoadOutImage)
                    .load(operatorDetail.imageUrl.asString(itemView.context))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource?.run {
                                colorFilter = ColorMatrixColorFilter(NEGATIVE)
                                target?.onResourceReady(this, null)
                                return true
                            }

                            return false
                        }
                    })
                    .into(binding.ivLoadOutImage)

                binding.tvLoadOutType.text = ""
            }
        }

        class OperatorDetailDividerViewHolder(binding: OperatorDetailDividerItemRowBinding) : ViewHolder(binding.root) {

            override fun <T : OperatorDetails> bind(operatorDetail: T) {
                if (operatorDetail !is OperatorDetails.OperatorDetailsDivider) {
                    return
                }
            }
        }

        class OrganizationViewHolder(private val binding: OperatorDetailTextItemRowBinding) : ViewHolder(binding.root) {

            override fun <T : OperatorDetails> bind(operatorDetail: T) {
                if (operatorDetail !is OperatorDetails.OrganizationEntity) {
                    return
                }

                binding.tvText.text = operatorDetail.name.asString(itemView.context)

                GlideApp.with(binding.tvText)
                    .load(operatorDetail.imageUrl.asString(itemView.context))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            binding.tvText.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            binding.tvText.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null)
                        }
                    })
            }
        }
    }
}