package eamato.funn.r6companion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.roulette_operator_row.view.*

class RouletteOperatorsAdapter :
    ListAdapter<RouletteOperator, RouletteOperatorsAdapter.RouletteOperatorsViewHolder>(RouletteOperator.ROULETTE_OPERATOR_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouletteOperatorsViewHolder {
        return RouletteOperatorsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.roulette_operator_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RouletteOperatorsViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    fun getItemAtPosition(position: Int): RouletteOperator {
        return getItem(position)
    }

    class RouletteOperatorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val iv_operator_image = itemView.iv_operator_image
        private val tv_operator_name = itemView.tv_operator_name
        private val pb_waiting = itemView.pb_waiting
        private val cb_is_selected = itemView.cb_is_selected
        private val iv_operator_icon = itemView.iv_operator_icon

        fun setItem(rouletteOperator : RouletteOperator) {
            GlideApp.with(itemView.context)
                .load(rouletteOperator.imgLink)
                .override(ROULETTE_OPERATOR_IMAGE_WIDTH, ROULETTE_OPERATOR_IMAGE_HEIGHT)
                .transform(ImageResizeTransformation(ROULETTE_OPERATOR_IMAGE_WIDTH, ROULETTE_OPERATOR_IMAGE_HEIGHT))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.transparent_300)
                .error(R.drawable.no_data_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .listener(object : IDoAfterTerminateGlide {
                    override fun doAfterTerminate() {
                        pb_waiting.hide()
                    }
                })
                .dontAnimate()
                .into(iv_operator_image)

            tv_operator_name.text = rouletteOperator.name

            cb_is_selected.isChecked = rouletteOperator.isSelected

            GlideApp.with(itemView.context)
                .load(rouletteOperator.operatorIconLink)
                .override(ROULETTE_OPERATOR_ICON_WIDTH, ROULETTE_OPERATOR_ICON_HEIGHT)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.transparent_75)
                .dontAnimate()
                .into(iv_operator_icon)
        }

    }

}