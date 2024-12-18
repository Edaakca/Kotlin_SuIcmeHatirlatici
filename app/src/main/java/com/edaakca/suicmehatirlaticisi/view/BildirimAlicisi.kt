package com.edaakca.suicmehatirlaticisi.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.edaakca.suicmehatirlaticisi.R

class BildirimAlicisi : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Bildirim kanalını oluştur
        createNotificationChannel(notificationManager)

        // Bildirim oluştur
        val notificationBuilder = NotificationCompat.Builder(context, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_water_drop) // Bildirim simgesi
            .setContentTitle("Su İçme Hatırlatıcı")
            .setContentText("Su içmeyi unutmayın!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        // Bildirimi göster
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "CHANNEL_ID"
            val channelName = "Su İçme Hatırlatıcı"
            val channelDescription = "Su içme hatırlatıcı bildirimleri için."

            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = channelDescription
            }

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}