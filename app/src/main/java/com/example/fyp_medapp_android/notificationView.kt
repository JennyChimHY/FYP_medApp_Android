package com.example.fyp_medapp_android

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class AlarmReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?) {
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
//                .setLargeIcon(apiDomain + "/images/MedApp_medicinePicture/" + picture + ".jpg")
//                .setStyle(NotificationCompat.BigPictureStyle()
//                    .bigPicture(myBitmap)
//                    .bigLargeIcon(null))
                    //Image: xml Image Loader
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
                            RemoteViews(appContext.packageName, R.layout.notification_layout)
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