package eamato.funn.r6companion.adapters.recycler_view_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.RouletteOperator
import eamato.funn.r6companion.utils.*
import eamato.funn.r6companion.utils.glide.GlideApp
import eamato.funn.r6companion.utils.glide.ImageResizeTransformation

class SimpleOperatorsAdapter :
    ListAdapter<RouletteOperator, SimpleOperatorsAdapter.SimpleOperatorsViewHolder>(RouletteOperator.ROULETTE_OPERATOR_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleOperatorsViewHolder {
        return SimpleOperatorsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.simple_operator_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SimpleOperatorsViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    class SimpleOperatorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var iv_operator_image: ImageView? = null

        init {
            iv_operator_image = itemView.findViewById(R.id.iv_operator_image)
        }

        fun setItem(operator: RouletteOperator) {
            iv_operator_image?.let {
                GlideApp.with(it)
                    .load(operator.imgLink)
                    .override(SIMPLE_OPERATOR_IMAGE_WIDTH, SIMPLE_OPERATOR_IMAGE_HEIGHT)
                    .transform(ImageResizeTransformation(SIMPLE_OPERATOR_IMAGE_WIDTH, SIMPLE_OPERATOR_IMAGE_HEIGHT))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.transparent_300)
                    .error(R.drawable.no_data_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade(200))
                    .dontAnimate()
                    .into(it)
            }
        }
    }

}