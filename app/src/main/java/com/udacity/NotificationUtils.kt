package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    notificationID: Int,
    libraryName: String
) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra("library_name", libraryName)

    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        MainActivity.CHANNEL_ID
    )
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_notification, applicationContext.getString(R.string.check_status), pendingIntent)

    notify(notificationID, builder.build())
}