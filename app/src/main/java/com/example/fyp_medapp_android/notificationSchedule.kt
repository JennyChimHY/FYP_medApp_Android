package com.example.fyp_medapp_android

//https://www.youtube.com/watch?v=_Z2S63O-1HE
//https://www.youtube.com/watch?v=T8cNaXSkeSM  seems better?

//https://medium.com/@nipunvirat0/how-to-schedule-alarm-in-android-using-alarm-manager-7a1c3b23f1bb

import android.content.Context
import java.time.LocalDateTime

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import java.time.ZoneId

data class NotiAlarmItem(
    val alarmTime : LocalDateTime,
    val notiType: String,
    val message : String,
    val picture : String?
)

interface NotiAlarmScheduler {
    fun schedule(notiAlarmItem: NotiAlarmItem)
    fun cancel(notiAlarmItem: NotiAlarmItem)
}

class AlarmSchedulerImpl(
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

//TODO: implement location service alarm, every 5 mins





