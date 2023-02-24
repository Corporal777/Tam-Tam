package org.otunjargych.tamtam.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.ui.main.MainActivity
import org.otunjargych.tamtam.util.NotificationUtil


class FcmMessagingService : FirebaseMessagingService() {

    private val notificationUtil: NotificationUtil by inject<NotificationUtil>()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        processMessage(remoteMessage)
    }

    private fun processMessage(remoteMessage: RemoteMessage) {
        notificationUtil.createSimpleNotification(
            mapOf(
                DATA_TITLE to remoteMessage.notification?.title,
                DATA_BODY to remoteMessage.notification?.body,
                DATA_SIMPLE_NOTIFICATION_ID to "111"
            )
        )
    }



    companion object {
        private const val DATA_TITLE = "title"
        private const val DATA_BODY = "body"
        private const val DATA_SIMPLE_NOTIFICATION_ID = "code"
        private const val DATA_NOTE_ID = "note_id"

    }
}