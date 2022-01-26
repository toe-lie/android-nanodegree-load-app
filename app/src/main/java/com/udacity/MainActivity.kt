package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        createDefaultNotificationChannel()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if (custom_button.buttonState != ButtonState.Loading) {
                markDownloadButtonStateAsClicked()
                download()
            }
        }
    }

    private fun createDefaultNotificationChannel() {
        createChannel(
            getString(R.string.default_notification_channel_id),
            getString(R.string.default_notification_channel_name)
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.default_notification_channel_description)

            val notificationManager =
                ContextCompat.getSystemService(
                    this,
                    NotificationManager::class.java
                )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                val notificationManager = ContextCompat.getSystemService(
                    applicationContext, NotificationManager::class.java
                ) as NotificationManager
                notificationManager.sendNotification(applicationContext)
            }
        }
    }

    private fun markDownloadButtonStateAsClicked() {
        custom_button.setState(ButtonState.Clicked)
    }

    private fun download() {
        val checkedRadioButtonId = radio_group.checkedRadioButtonId
        if (checkedRadioButtonId == -1) {
            Toast.makeText(
                this,
                getString(R.string.toast_message_required_to_select_file),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val url = getSelectedDownloadUrl(checkedRadioButtonId)
        if (url.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.toast_message_invalid_url),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresCharging(false)
        }

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun getSelectedDownloadUrl(checkedRadioButtonId: Int): String {
        return when (checkedRadioButtonId) {
            R.id.radio_button_glide -> GLIDE_URL
            R.id.radio_button_retrofit -> RETROFIT_URL
            R.id.radio_button_loadapp -> LOAD_APP_URL
            else -> ""
        }
    }

    companion object {
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
