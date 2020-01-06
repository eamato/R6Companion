package eamato.funn.r6companion.firebase.things

import com.google.gson.annotations.SerializedName

abstract class LocalizedRemoteConfigEntity(
    @SerializedName("en")
    val en: String? = null,
    @SerializedName("ru")
    val ru: String? = null
)

class ComingSoon : LocalizedRemoteConfigEntity()

class OurMission : LocalizedRemoteConfigEntity()