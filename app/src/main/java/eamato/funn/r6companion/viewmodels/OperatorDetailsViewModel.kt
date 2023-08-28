package eamato.funn.r6companion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eamato.funn.r6companion.R
import eamato.funn.r6companion.entities.CompanionOperator
import eamato.funn.r6companion.entities.OperatorDetails
import eamato.funn.r6companion.utils.UIText

class OperatorDetailsViewModel : ViewModel() {

    private val _operatorDetails = MutableLiveData<List<OperatorDetails>?>(null)
    val operatorDetails: LiveData<List<OperatorDetails>?> = _operatorDetails

    fun createListOfDetailsFor(operator: CompanionOperator) {
        val operatorDetails = mutableListOf<OperatorDetails>()

        operator.wideImgLink?.let { OperatorDetails.OperatorDetailsImage(UIText.SimpleString(it)) }?.run { operatorDetails.add(this) }
        operatorDetails.add(OperatorDetails.OperatorDetailsTitle(UIText.ResourceString(R.string.operator_details_organization)))
        operator.squad?.let { OperatorDetails.OrganizationEntity(
            UIText.SimpleString(it.name),
            UIText.SimpleString(it.iconLink))
        }?.run { operatorDetails.add(this) }
        operatorDetails.add(OperatorDetails.OperatorDetailsDivider)

        operatorDetails.add(OperatorDetails.OperatorDetailsTitle(UIText.ResourceString(R.string.operator_details_role)))
        operator.role?.let { OperatorDetails.OperatorDetailsText(UIText.SimpleString(it)) }?.run { operatorDetails.add(this) }
        operatorDetails.add(OperatorDetails.OperatorDetailsDivider)

        operatorDetails.add(OperatorDetails.OperatorDetailsTitle(UIText.ResourceString(R.string.operator_details_stats)))
        operator.speedRating?.let { OperatorDetails.OperatorDetailsStat(UIText.ResourceString(R.string.operator_details_speed), it) }?.run { operatorDetails.add(this) }
        operator.armorRating?.let { OperatorDetails.OperatorDetailsStat(UIText.ResourceString(R.string.operator_details_armor), it) }?.run { operatorDetails.add(this) }
        operatorDetails.add(OperatorDetails.OperatorDetailsDivider)

        operatorDetails.add(OperatorDetails.OperatorDetailsTitle(UIText.ResourceString(R.string.operator_details_loadout)))
        operatorDetails.add(OperatorDetails.OperatorDetailsSubtitle(UIText.ResourceString(R.string.operator_details_primaries)))
        operator.equipment?.let {
            it.primaries?.filterNotNull()?.mapNotNull { primaries ->
                if (primaries.name != null && primaries.iconLink != null) {
                    OperatorDetails.OperatorDetailsLoadOutEntity(
                        UIText.SimpleString(primaries.name),
                        UIText.SimpleString(primaries.iconLink),
                        UIText.SimpleString(primaries.typeText)
                    )
                } else {
                    null
                }
            }
        }?.run { operatorDetails.addAll(this) }
        operatorDetails.add(OperatorDetails.OperatorDetailsSubtitle(UIText.ResourceString(R.string.operator_details_secondaries)))
        operator.equipment?.let {
            it.secondaries?.filterNotNull()?.mapNotNull { secondaries ->
                if (secondaries.name != null && secondaries.iconLink != null) {
                    OperatorDetails.OperatorDetailsLoadOutEntity(
                        UIText.SimpleString(secondaries.name),
                        UIText.SimpleString(secondaries.iconLink),
                        UIText.SimpleString(secondaries.typeText)
                    )
                } else {
                    null
                }
            }
        }?.run { operatorDetails.addAll(this) }
        operatorDetails.add(OperatorDetails.OperatorDetailsSubtitle(UIText.ResourceString(R.string.operator_details_gadgets)))
        operator.equipment?.let {
            it.devices?.filterNotNull()?.mapNotNull { gadgets ->
                if (gadgets.name != null && gadgets.iconLink != null) {
                    OperatorDetails.OperatorDetailsLoadOutEntity(
                        UIText.SimpleString(gadgets.name),
                        UIText.SimpleString(gadgets.iconLink)
                    )
                } else {
                    null
                }
            }
        }?.run { operatorDetails.addAll(this) }
        operatorDetails.add(OperatorDetails.OperatorDetailsSubtitle(UIText.ResourceString(R.string.operator_details_ability)))
        operator.equipment?.skill?.let {
            if (it.name != null && it.iconLink != null) {
                OperatorDetails.OperatorDetailsAbilityEntity(
                    UIText.SimpleString(it.name),
                    UIText.SimpleString(it.iconLink)
                )
            } else {
                null
            }
        }?.run { operatorDetails.add(this) }

        _operatorDetails.value = operatorDetails.toList()
    }
}