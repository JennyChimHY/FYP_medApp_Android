package com.example.fyp_medapp_android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessaging


//Alarm Function and Firebase Initialization

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "LocalStore")
var patientFCM_token = ""
class AlarmApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val channelId = "alarm_id"
        val channelName = "alarm_name"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)


        FirebaseApp.initializeApp(this) //Firebase Initialization
        //https://medappserver.f0226942.hkbu.app/images/MedApp_utilities/success.png
        //https://firebase.google.com/docs/cloud-messaging/android/first-message?hl=zh&authuser=0
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            patientFCM_token = task.result
            Log.e("myToken", "" + patientFCM_token)



//            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
//            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }
}