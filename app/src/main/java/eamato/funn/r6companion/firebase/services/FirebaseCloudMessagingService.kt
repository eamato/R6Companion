package eamato.funn.r6companion.firebase.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import eamato.funn.r6companion.utils.notifications.R6NotificationManager

class FirebaseCloudMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val notification = p0.notification
        R6NotificationManager.showNotification(
            context = this,
            notificationId = 1,
            title = notification?.title,
            content = notification?.body
        )
    }

}