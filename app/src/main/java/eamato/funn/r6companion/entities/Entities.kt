package eamato.funn.r6companion.entities

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import eamato.funn.r6companion.utils.UIText
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
        var operatorIconLink: String?,
        @SerializedName("armor_rating")
        val armorRating: Int?,
        @SerializedName("equipment")
        val equipment: Equipment?,
        @SerializedName("speed_rating")
        val speedRating: Int?,
        @SerializedName("squad")
        val squad: Squad?,
        @SerializedName("role")
        val role: String?,
        @SerializedName("wide_img_link")
        val wideImgLink: String?,
    ) {
        data class Equipment(
            @SerializedName("devices")
            val devices: List<Device?>?,
            @SerializedName("primaries")
            val primaries: List<Primary?>?,
            @SerializedName("secondaries")
            val secondaries: List<Secondary?>?,
            @SerializedName("skill")
            val skill: Skill?
        ) {
            data class Device(
                @SerializedName("icon_link")
                val iconLink: String?,
                @SerializedName("name")
                val name: String?
            )

            data class Primary(
                @SerializedName("icon_link")
                val iconLink: String?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("type_text")
                val typeText: String?
            )

            data class Secondary(
                @SerializedName("icon_link")
                val iconLink: String?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("type_text")
                val typeText: String?
            )

            data class Skill(
                @SerializedName("icon_link")
                val iconLink: String?,
                @SerializedName("name")
                val name: String?
            )
        }

        data class Squad(
            @SerializedName("icon_link")
            val iconLink: String?,
            @SerializedName("name")
            val name: String?
        )
    }
}

@Parcelize
data class RouletteOperator(
    private var rImgLink: String?,
    private var rName: String?,
    private var rOperatorIconLink: String?,
    var isSelected: Boolean = false
) : Operators.Operator(
    null, rImgLink, rName, rOperatorIconLink,
    null, null, null, null, null, null
), Parcelable {

    constructor(operator: Operators.Operator) : this(
        operator.imgLink,
        operator.name,
        operator.operatorIconLink
    )

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
    val armorRating: Int?,
    val id: Int?,
    val name: String?,
    val role: String?,
    val speedRating: Int?,
    val imageUrl: String?,
    val ctuName: String?
) {
    companion object {
        const val ROLE_RECRUIT = "recruit"
        const val ROLE_DEFENDER = "DEFENDER"
        const val ROLE_ATTACKER = "ATTACKER"
    }
}

@Parcelize
data class CompanionOperator constructor(
    val id: Int?,
    var imgLink: String?,
    var wideImgLink: String?,
    var name: String?,
    var operatorIconLink: String?,
    val armorRating: Int?,
    val equipment: Equipment?,
    val speedRating: Int?,
    val squad: Squad?,
    val role: String?,
) : Parcelable {

    @Parcelize
    data class Equipment(
        @SerializedName("devices")
        val devices: List<Device?>?,
        @SerializedName("primaries")
        val primaries: List<Primary?>?,
        @SerializedName("secondaries")
        val secondaries: List<Secondary?>?,
        @SerializedName("skill")
        val skill: Skill?
    ) : Parcelable {

        @Parcelize
        data class Device(
            @SerializedName("icon_link")
            val iconLink: String?,
            @SerializedName("name")
            val name: String?
        ) : Parcelable

        @Parcelize
        data class Primary(
            @SerializedName("icon_link")
            val iconLink: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("type_text")
            val typeText: String?
        ) : Parcelable

        @Parcelize
        data class Secondary(
            @SerializedName("icon_link")
            val iconLink: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("type_text")
            val typeText: String?
        ) : Parcelable

        @Parcelize
        data class Skill(
            @SerializedName("icon_link")
            val iconLink: String?,
            @SerializedName("name")
            val name: String?
        ) : Parcelable
    }

    @Parcelize
    data class Squad(
        @SerializedName("icon_link")
        val iconLink: String?,
        @SerializedName("name")
        val name: String?
    ) : Parcelable
}

interface IViewType {
    fun getItemViewType(): Int
}

sealed class OperatorDetails : IViewType {

    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_TITLE = 2
        const val VIEW_TYPE_SUBTITLE = 3
        const val VIEW_TYPE_TEXT = 4
        const val VIEW_TYPE_STAT = 5
        const val VIEW_TYPE_LOAD_OUT_ENTITY = 6
        const val VIEW_TYPE_DIVIDER = 7
        const val VIEW_TYPE_ABILITY_ENTITY = 8
        const val VIEW_TYPE_ORGANIZATION_ENTITY = 9
    }

    override fun getItemViewType(): Int {
        return when (this) {
            is OperatorDetailsImage -> VIEW_TYPE_IMAGE
            is OperatorDetailsTitle -> VIEW_TYPE_TITLE
            is OperatorDetailsSubtitle -> VIEW_TYPE_SUBTITLE
            is OperatorDetailsText -> VIEW_TYPE_TEXT
            is OperatorDetailsStat -> VIEW_TYPE_STAT
            is OperatorDetailsLoadOutEntity -> VIEW_TYPE_LOAD_OUT_ENTITY
            is OperatorDetailsAbilityEntity -> VIEW_TYPE_ABILITY_ENTITY
            is OperatorDetailsDivider -> VIEW_TYPE_DIVIDER
            is OrganizationEntity -> VIEW_TYPE_ORGANIZATION_ENTITY
        }
    }

    data class OperatorDetailsImage(val imageUrl: UIText) : OperatorDetails()
    data class OperatorDetailsTitle(val title: UIText) : OperatorDetails()
    data class OperatorDetailsSubtitle(val subtitle: UIText) : OperatorDetails()
    data class OperatorDetailsText(val text: UIText) : OperatorDetails()
    data class OperatorDetailsStat(val name: UIText, val value: Int) : OperatorDetails()
    data class OperatorDetailsLoadOutEntity(
        val name: UIText,
        val imageUrl: UIText,
        val typeText: UIText? = null
    ) : OperatorDetails()
    data class OperatorDetailsAbilityEntity(
        val name: UIText,
        val imageUrl: UIText
    ) : OperatorDetails()
    object OperatorDetailsDivider : OperatorDetails()
    data class OrganizationEntity(val name: UIText, val imageUrl: UIText) : OperatorDetails()
}