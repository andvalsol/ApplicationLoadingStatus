package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.NullPointerException


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private var libraryName = ""

    private var urlForDownload = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        rg_libraries.setOnCheckedChangeListener { _, checkedId ->
            val url = when (checkedId) {
                R.id.radio_glide -> {
                    libraryName = getString(R.string.glide)
                    listOfDownloadableLinks[1]
                }
                R.id.radio_load_app -> {
                    libraryName = getString(R.string.load_app)
                    listOfDownloadableLinks[0]
                }
                R.id.radio_retrofit -> {
                    libraryName = getString(R.string.retrofit)
                    listOfDownloadableLinks[2]
                }
                else -> throw NullPointerException()
            }

            urlForDownload = url
        }

        custom_button.setOnClickListener {
            if (urlForDownload.isNotEmpty()) download(urlForDownload)
            else Toast.makeText(
                this,
                "Please specify the url you want to download",
                Toast.LENGTH_LONG
            ).show()
        }

        createChannel(CHANNEL_ID, getString(R.string.channel_name))
    }

    private fun sendNotification(notificationID: Int) {
        notificationManager.sendNotification(
            getString(R.string.notification_description),
            this,
            notificationID ,
            libraryName
         )
    }

    private fun createChannel(channelID: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            notificationManager = getSystemService(NotificationManager::class.java)!!.apply {
                createNotificationChannel(notificationChannel)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                // The download has been completed
                // Animate the custom button
                custom_button.animateButton()

                // Send the notification telling the user that the download has complete
                sendNotification(downloadID.toInt())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver
        unregisterReceiver(receiver)
    }

    private fun download(URL: String) {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue
    }

    companion object {

        private val listOfDownloadableLinks = listOf(
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip",
            "https://github.com/bumptech/glide",
            "https://github.com/square/retrofit"
        )

        const val CHANNEL_ID = "channelId"
    }
}
