package eamato.funn.r6companion.entities

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class Operators(
    @SerializedName("operators")
    var operators: List<Operator?>?
) {
    open class Operator(
        @SerializedName("id")
        val id: Int?,
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
    null, rImgLink, rName, rOperatorIconLink
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

class R6StatsOperators : ArrayList<R6StatsOperators.R6StatsOperatorsItem>() {
    data class R6StatsOperatorsItem(
        @SerializedName("armor_rating")
        val armorRating: Int?,
        @SerializedName("ctu")
        val ctu: Ctu?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("role")
        val role: String?,
        @SerializedName("speed_rating")
        val speedRating: Int?
    ) {
        data class Ctu(
            @SerializedName("country")
            val country: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("internal_name")
            val internalName: String?,
            @SerializedName("name")
            val name: String?
        )
    }
}

data class CompositeOperator(
    val armorRating: Int?, val id: Int?, val name: String?, val role: String?, val speedRating: Int?,
    val imageUrl: String?, val ctuName: String?
) {
    companion object {
        const val ROLE_RECRUIT = "recruit"
        const val ROLE_DEFENDER = "defender"
        const val ROLE_ATTACKER = "attacker"
    }
}