package eamato.funn.r6companion.entities.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouletteFragmentArgument(
    val operatorNames: List<String>?
) : Parcelable