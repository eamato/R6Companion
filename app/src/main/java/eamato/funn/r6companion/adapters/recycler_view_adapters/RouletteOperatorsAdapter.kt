package eamato.funn.r6companion.adapters.recycler_view_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.RouletteOperator
import eamato.funn.r6companion.utils.IDoAfterTerminateGlide
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.utils.glide.GlideApp
import eamato.funn.r6companion.utils.glide.ImageResizeTransformation

class RouletteOperatorsAdapter :
    ListAdapter<RouletteOperator, RouletteOperatorsAdapter.RouletteOperatorsViewHolder>(RouletteOperator.ROULETTE_OPERATOR_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouletteOperatorsViewHolder {
        return RouletteOperatorsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.roulette_operator_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RouletteOperatorsViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    fun getItemAtPosition(position: Int): RouletteOperator {
        return getItem(position)
    }

    class RouletteOperatorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var iv_operator_image: ImageView? = null
        private var tv_operator_name: TextView? = null
        private var pb_waiting: ContentLoadingProgressBar? = null
        private var cb_is_selected: CheckBox? = null
        private var iv_operator_icon: ImageView? = null

        init {
            iv_operator_image = itemView.findViewById(R.id.iv_operator_image)
            tv_operator_name = itemView.findViewById(R.id.tv_operator_name)
            pb_waiting = itemView.findViewById(R.id.pb_waiting)
            cb_is_selected = itemView.findViewById(R.id.cb_is_selected)
            iv_operator_icon = itemView.findViewById(R.id.iv_operator_icon)
        }

        fun setItem(rouletteOperator : RouletteOperator) {
            iv_operator_image?.let {
                GlideApp.with(it)
                    .load(rouletteOperator.imgLink)
                    .override(ROULETTE_OPERATOR_IMAGE_WIDTH, ROULETTE_OPERATOR_IMAGE_HEIGHT)
                    .transform(ImageResizeTransformation(ROULETTE_OPERATOR_IMAGE_WIDTH, ROULETTE_OPERATOR_IMAGE_HEIGHT))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.transparent_300)
                    .error(R.drawable.no_data_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .listener(object : IDoAfterTerminateGlide {
                        override fun doAfterTerminate() {
                            pb_waiting?.hide()
                        }
                    })
                    .dontAnimate()
                    .into(it)
            }

            tv_operator_name?.text = rouletteOperator.name

            cb_is_selected?.isChecked = rouletteOperator.isSelected

            iv_operator_icon?.let {
                GlideApp.with(it)
                    .load(rouletteOperator.operatorIconLink)
                    .override(ROULETTE_OPERATOR_ICON_WIDTH, ROULETTE_OPERATOR_ICON_HEIGHT)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.transparent_75)
                    .dontAnimate()
                    .into(it)
            }
        }

    }

}