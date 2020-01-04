package eamato.funn.r6companion.entities

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Operators(
    @SerializedName("operators")
    var operators: List<Operator?>?
) {
    open class Operator(
        @SerializedName("img_link")
        var imgLink: String?,
        @SerializedName("name")
        var name: String?,
        @SerializedName("operator_icon_link")
        var operatorIconLink: String?
    )
}

@Parcelize
data class RouletteOperator(
    private var rImgLink: String?,
    private var rName: String?,
    private var rOperatorIconLink: String?,
    var isSelected: Boolean = false
) : Operators.Operator(
    rImgLink, rName, rOperatorIconLink
), Parcelable {

    constructor(operator: Operators.Operator) : this(operator.imgLink, operator.name, operator.operatorIconLink)

    companion object {
        val ROULETTE_OPERATOR_DIFF_CALLBACK = object : DiffUtil.ItemCallback<RouletteOperator>() {
            override fun areItemsTheSame(
                oldItem: RouletteOperator,
                newItem: RouletteOperator
            ): Boolean {
                return oldItem.imgLink == newItem.imgLink &&
                        oldItem.name == newItem.name &&
                        oldItem.operatorIconLink == newItem.operatorIconLink
            }

            override fun areContentsTheSame(
                oldItem: RouletteOperator,
                newItem: RouletteOperator
            ): Boolean {
                return oldItem.imgLink == newItem.imgLink &&
                        oldItem.name == newItem.name &&
                        oldItem.operatorIconLink == newItem.operatorIconLink &&
                        oldItem.isSelected == newItem.isSelected
            }
        }
    }

}

@Parcelize
data class ParcelableListOfRouletteOperators(val rouletteOperators: List<RouletteOperator>) :
    ArrayList<RouletteOperator>(rouletteOperators), Parcelable