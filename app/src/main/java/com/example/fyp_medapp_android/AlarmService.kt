package com.example.fyp_medapp_android

//https://www.youtube.com/watch?v=_Z2S63O-1HE
//https://www.youtube.com/watch?v=T8cNaXSkeSM  seems better?

//https://medium.com/@nipunvirat0/how-to-schedule-alarm-in-android-using-alarm-manager-7a1c3b23f1bb

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager

import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.gms.location.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit


//=================== SET ALARM SECTION ===================
data class NotiAlarmItem(
    val alarmTime: LocalDateTime,
    val notiType: String,
    val message: String,
    val picture: String?
)

data class LocationAlarmItem(
    val alarmTime: LocalDateTime,
    var locationUser: String
)


//Notification Alarm
interface NotiAlarmScheduler {
    fun schedule(notiAlarmItem: NotiAlarmItem)
    fun cancel(notiAlarmItem: NotiAlarmItem)
}

class NotiAlarmSchedulerImpl(
    private val context: Context
) : NotiAlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(notiAlarmItem: NotiAlarmItem) {
        val intent = Intent(context, NotiAlarmReceiver::class.java).apply {
            putExtra("EXTRA_NOTITYPE", notiAlarmItem.notiType)
            putExtra("EXTRA_MESSAGE", notiAlarmItem.message)
            putExtra("EXTRA_PICTURE", notiAlarmItem.picture)
        }

        val alarmTime =
            notiAlarmItem.alarmTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L
        alarmManager.setExactAndAllowWhileIdle(  //here
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            PendingIntent.getBroadcast(
                context,
                notiAlarmItem.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        Log.e("Alarm", "Noti Alarm set at $alarmTime")
    }

    override fun cancel(notiAlarmItem: NotiAlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                notiAlarmItem.hashCode(),
                Intent(context, NotiAlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}

//Location Alarm
interface LocationAlarmScheduler {
    fun schedule(locationAlarmItem: LocationAlarmItem)
    fun cancel(locationAlarmItem: LocationAlarmItem)
}

class LocationAlarmSchedulerImpl(
    private val context: Context
) : LocationAlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(locationAlarmItem: LocationAlarmItem) {
        val intent = Intent(context, LocationAlarmReceiver::class.java).apply {
            putExtra("EXTRA_LOCATIONUSER", locationAlarmItem.locationUser)
        }

        val alarmTime =
            locationAlarmItem.alarmTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000L

//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 5*60*1000, pendingIntent);
        alarmManager.setExactAndAllowWhileIdle(  //here
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            PendingIntent.getBroadcast(
                context,
                locationAlarmItem.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        Log.e("Alarm", "Location Alarm set at $alarmTime")
    }

    override fun cancel(locationAlarmItem: LocationAlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                locationAlarmItem.hashCode(),
                Intent(context, LocationAlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

}


//================= ALARM ONRECEIVE SECTION ==================


class NotiAlarmReceiver : BroadcastReceiver() { //AndoridManifest declared enable NotiAlarmReceiver
    override fun onReceive(context: Context?, intent: Intent?) {
//        context?.unregisterReceiver(this)
        Log.d("NotiAlarmReceiver", "Noti Alarm Received")

        val notiType = intent?.getStringExtra("EXTRA_NOTITYPE") ?: return //string notification type
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        val picture =
            intent?.getStringExtra("EXTRA_PICTURE") ?: return  //string name of the picture
        val channelId = "alarm_id"
        context?.let { ctx ->
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val builder = NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) //mandatory
                .setContentTitle("Reminder") //mandatory
                .setContentText(message)    //mandatory
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            // inflate the layout and set the values to our UI IDs
            val appContext = ctx.applicationContext

            val imageLoader = ImageLoader(appContext)
            val request = ImageRequest.Builder(appContext)
                .data("https://medappserver.f0226942.hkbu.app/images/MedApp_medicinePicture/$picture.jpg")
                .target { drawable ->
                    // Handle the result.
                    MainScope().launch {//launch UI coroutine
                        val remoteViews =
                            RemoteViews(
                                appContext.packageName,
                                R.layout.notification_layout
                            ) //noti custom layout in xml
                        remoteViews.setTextViewText(
                            R.id.title,
                            "$notiType Reminder"
                        )  //reminder type
                        remoteViews.setTextViewText(R.id.text, message) //reminder message
                        remoteViews.setImageViewBitmap(R.id.image, drawable.toBitmap())

                        builder.setCustomBigContentView(remoteViews)
                        notificationManager.notify(1, builder.build())
                    }
                }
                .build()

            val disposable = imageLoader.enqueue(request)
            disposable.dispose()

            println("Reminder pushed")

        }
    }
}


//using Alarm as background service to record the location
class LocationAlarmReceiver : BroadcastReceiver() {  //AndoridManifest declared enable LocationAlarmReceiver

    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates
    private lateinit var locationRequest: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient
// has a new Location
    private lateinit var locationCallback: LocationCallback

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("LocationAlarmReceiver", "Location Alarm Received")

        var locationUser = intent?.getStringExtra("EXTRA_LOCATIONUSER") ?: return
        var currentLocation: Location? = null


        //call ktor to save in DB
        //also delete the location record 1 week ago?

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context!!) //Initialize fusedLocationProviderClient
        locationRequest = LocationRequest().apply {    //Initialize locationRequest
            // Sets the desired interval for active location updates. This interval is inexact.
            interval = TimeUnit.SECONDS.toMillis(50)  //500 = 5 mins?

            // Sets the fastest rate for active location updates. This interval is exact, and your application will never receive updates more frequently than this value
            fastestInterval = TimeUnit.SECONDS.toMillis(30)

            // Sets the maximum time when batched location updates are delivered. Updates may be delivered sooner than this interval
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        //Initialize locationCallback.
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult?.lastLocation?.let {
                    currentLocation = it
                } ?: {
                    Log.d("abc", "Location information isn't available.")
                }

                var latitude = currentLocation!!.latitude
                var longitude = currentLocation!!.longitude

                Log.d(
                    "abc",
                    "latitude: $latitude, longitude: $longitude"
                )
            }
        }

        //let the FusedLocationProviderClient know that you want to receive updates. So Subscribe to location changes.
        if (ActivityCompat.checkSelfPermission(
                context.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
           //no permission, no action
            return
        }

        //call fusedLocationProviderClient to receive location updates.
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }



//        //When the app no longer needs access to location information, it’s important to unsubscribe from location updates.
//        val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
//        removeTask.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Log.d("abc", "Location Callback removed.")
//            } else {
//                Log.d("abc", "Failed to remove Location Callback.")
//            }
//        }


//    fun cancel(notiAlarmItem: NotiAlarmItem) {
//        alarmManager.cancel(
//            PendingIntent.getBroadcast(
//                context,
//                notiAlarmItem.hashCode(),
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        )
//    }

}

//removed function, now ActivityCompat.checkSelfPermission(context!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTEDusing
//fun isLocationPermissionGranted(applicationContext: Context): Boolean {
//    //safety check for permission again, already requested at MainActivity(initial page) before
//    return ActivityCompat.checkSelfPermission(
//        applicationContext,
//        android.Manifest.permission.ACCESS_COARSE_LOCATION
//    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//        applicationContext,
//        android.Manifest.permission.ACCESS_FINE_LOCATION
//    ) != PackageManager.PERMISSION_GRANTED
//    //take no action if granted
//}





