package com.example.fyp_medapp_android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import co.yml.charts.common.extensions.isNotNull
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */

    //generate the notifications
    //attach the notification with the custom layout
    //show the notification
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //receive the notification
        if (remoteMessage.getNotification() != null) {
            generateNotification(
                remoteMessage.notification!!.title!!,
                remoteMessage.notification!!.body!!
            )
        }
    }

    //generate the notification
    fun generateNotification(title: String, message: String) {

        val channelId = "firebase_notification"
        val channelName = "firebase_notification"

        //create intent because when the user click on notification, the app will open
        val intent = Intent(this, MainActivity::class.java)

        //clear all the activities and put this(MainActivity) at the top priority
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //we use this pending activity only once( it will destroy after used once)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        Log.d("MyFirebaseMessagingService", "Firebase title $title")
        Log.d("MyFirebaseMessagingService", "Firebase message $message")
        //We use channel id , channel name (after Oreo update)
        //we create notification using NotificationBuilder
        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                //set icons,autoCancel , OnlyAlertOnce
                .setSmallIcon(R.drawable.calendar)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)


        //attach the builder with the notification layout(create getRemoteView method)
//        builder = builder.setContent(getRemoteView(title, message))

        //notificationManager(Android allows to put notification into the titleBar of your application)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //check user version must be greater than OreoVersion which is Code O(oh not zero)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //create an notificationChannel( all notifications must be assigned to a channel)
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        //get notify
        notificationManager.notify(0, builder.build())
    }

    //    //create an method remoteView
//    fun getRemoteView(title: String, message: String): RemoteViews {
//        val remoteView =
//            RemoteViews("com.company0ne.pushnotification_firebase_kotlin", R.layout.notification)
//
//        remoteView.setTextViewText(R.id.title, title)
//        remoteView.setTextViewText(R.id.message, message)
//        remoteView.setImageViewResource(R.id.app_logo, R.drawable.notification_icon)
//        return remoteView
//    }

//    override fun onNewToken(token: String) {
//        Log.d(TAG, "Refreshed token: $token")
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // FCM registration token to your app server.
//        sendTokenToServer(token)
//    }

//    /**
//     * Persist token to third-party servers.
//     *
//     * Modify this method to associate the user's FCM registration token with any server-side account
//     * maintained by your application.
//     *
//     * @param token The new token.
//     */
//    private fun sendTokenToServer(token: String?) {
//        // If you're running your own server, call API to send token and today's date for the user
//
//        // Example shown below with Firestore
//        // Add token and timestamp to Firestore for this user
//        val deviceToken = hashMapOf(
//            "token" to token,
//            "timestamp" to FieldValue.serverTimestamp(),
//        )
//        // Get user ID from Firebase Auth or your own server
//        Firebase.firestore.collection("fcmTokens").document("myuserid")
//            .set(deviceToken)
//    }


}