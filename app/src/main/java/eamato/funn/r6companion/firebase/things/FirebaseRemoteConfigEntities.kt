package eamato.funn.r6companion.firebase.things

import com.google.gson.annotations.SerializedName

abstract class LocalizedRemoteConfigEntity(
    @SerializedName("en")
    val en: String? = null,
    @SerializedName("ru")
    val ru: String? = null
)

abstract class LocalizedOurTeamRemoteConfigEntity(
    @SerializedName("en")
    val en: En? = null,
    @SerializedName("ru")
    val ru: Ru? = null
) {
    data class Position(
        @SerializedName("first_name")
        val firstName: String? = null,
        @SerializedName("image")
        val image: String? = null,
        @SerializedName("last_name")
        val lastName: String? = null,
        @SerializedName("positions")
        val positions: List<String?>? = null
    )

    data class En(
        @SerializedName("positions")
        val positions: List<Position?>?
    )

    data class Ru(
        @SerializedName("positions")
        val positions: List<Position?>?
    )
}

class ComingSoon : LocalizedRemoteConfigEntity()

class OurMission : LocalizedRemoteConfigEntity()

class OurTeam : LocalizedOurTeamRemoteConfigEntity()