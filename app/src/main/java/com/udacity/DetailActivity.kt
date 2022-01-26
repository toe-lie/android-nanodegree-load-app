package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

private const val NO_DOWNLOAD_ID = -1L

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setSupportActionBar(binding.toolbar)

        val downloadId = intent.getLongExtra(
            DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS,
            NO_DOWNLOAD_ID
        )
        cancelNotification(downloadId)
        showDownloadInfo(downloadId)
        setupOnClickListeners()
    }

    private fun cancelNotification(notificationId: Long) {
        if (notificationId == NO_DOWNLOAD_ID) {
            return
        }

        val notificationManager =
            ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            )
        notificationManager?.cancel(notificationId.toInt())
    }

    private fun showDownloadInfo(downloadId: Long) {
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val query = DownloadManager.Query()
        query.setFilterById(downloadId)

        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

            binding.content.fileName.text = title
            binding.content.status.text = getUserFriendlyStatus(status)
            binding.content.status.setTextColor(getTextColorByStatus(status))
        }

    }

    private fun setupOnClickListeners() {
        binding.content.okButton.setOnClickListener {
            finish()
        }
    }

    private fun getUserFriendlyStatus(status: Int): String {
        val statusResId = when (status) {
            DownloadManager.STATUS_SUCCESSFUL -> R.string.label_status_success
            DownloadManager.STATUS_FAILED -> R.string.label_status_fail
            else -> -1
        }

        return if (statusResId != -1) {
            getString(statusResId)
        } else {
            "-"
        }
    }

    private fun getTextColorByStatus(status: Int): Int {
        return when (status) {
            DownloadManager.STATUS_SUCCESSFUL -> Color.GREEN
            DownloadManager.STATUS_FAILED -> Color.RED
            else -> Color.BLACK
        }
    }
}
