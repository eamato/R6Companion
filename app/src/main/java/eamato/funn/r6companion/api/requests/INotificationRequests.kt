package eamato.funn.r6companion.api.requests

import eamato.funn.r6companion.api.ApiClient
import eamato.funn.r6companion.utils.R6_API_HOST
import eamato.funn.r6companion.utils.REGISTER_NOTIFICATION_PARAM_KEY
import eamato.funn.r6companion.utils.REGISTER_NOTIFICATION_TOKEN_PATH
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface INotificationRequests {

    companion object {
        fun getNotificationRequests(): INotificationRequests = ApiClient
            .getApiClientCoroutines(R6_API_HOST)
            .create(INotificationRequests::class.java)
    }

    @FormUrlEncoded
    @POST(REGISTER_NOTIFICATION_TOKEN_PATH)
    suspend fun registerNotificationToken(
        @Field(REGISTER_NOTIFICATION_PARAM_KEY, encoded = true) notificationToken: String
    ): Any
}