package com.example.fyp_medapp_android

//https://www.youtube.com/watch?v=_Z2S63O-1HE
//https://www.youtube.com/watch?v=T8cNaXSkeSM  seems better?

//https://medium.com/@nipunvirat0/how-to-schedule-alarm-in-android-using-alarm-manager-7a1c3b23f1bb

import android.content.Context
import java.time.LocalDateTime

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.ZoneId


//=================== SET ALARM SECTION ===================
data class NotiAlarmItem(
    val alarmTime : LocalDateTime,
    val notiType: String,
    val message : String,
    val picture : String?
)

data class LocationAlarmItem(
    val alarmTime : LocalDateTime,
    val notiType: String,
    val locationRecord : String
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

        val alarmTime = notiAlarmItem.alarmTime.atZone(ZoneId.systemDefault()).toEpochSecond()*1000L
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
        Log.e("Alarm", "Alarm set at $alarmTime")
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
            putExtra("EXTRA_NOTITYPE", locationAlarmItem.notiType)
            putExtra("EXTRA_LOCATIONRECORD", locationAlarmItem.locationRecord)
        }

        val alarmTime = locationAlarmItem.alarmTime.atZone(ZoneId.systemDefault()).toEpochSecond()*1000L
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
        Log.e("Alarm", "Alarm set at $alarmTime")
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


class NotiAlarmReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?) {
//        context?.unregisterReceiver(this)
        val notiType = intent?.getStringExtra("EXTRA_NOTITYPE") ?: return //string notification type
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        val picture = intent?.getStringExtra("EXTRA_PICTURE") ?: return  //string name of the picture
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
                            RemoteViews(appContext.packageName, R.layout.notification_layout) //noti custom layout in xml
                        remoteViews.setTextViewText(R.id.title, "$notiType Reminder")  //reminder type
                        remoteViews.setTextViewText(R.id.text, message) //reminder message
                        remoteViews.setImageViewBitmap(R.id.image, drawable.toBitmap())

                        builder.setCustomContentView(remoteViews)
                        notificationManager.notify(1, builder.build())
                    }
                }
                .build()
            val disposable = imageLoader.enqueue(request)
//            disposable.dispose()

            println("Reminder pushed")

        }
    }
}


//using Alarm as background service to record the location
class LocationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        //take the location record
    }

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





