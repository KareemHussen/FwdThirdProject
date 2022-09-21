package com.example.fwdthirdproject

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private  var linkTOBeDownloaded : String? = null
    private  var selectedNameDownload : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if(linkTOBeDownloaded != null) {
                custom_button.buttonState = ButtonState.Loading
                download()


            } else {
                val toast = Toast.makeText(applicationContext, "select anything to be downloaded", Toast.LENGTH_LONG).show()
            }
        }
        createChannel(CHANNEL_ID,getString(R.string.notification_title))
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)


            custom_button.buttonState = ButtonState.Completed

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val query = DownloadManager.Query()
            query.setFilterById(id!!)

            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                val intent = Intent(this@MainActivity , DetailActivity::class.java)

                var downloadStatus = "Fail"

                if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    downloadStatus = "Success"
                }
                intent.putExtra("status", downloadStatus)
                intent.putExtra("filename" ,selectedNameDownload )

                startActivity(intent)
                val toast = Toast.makeText(
                    applicationContext,
                    getString(R.string.notification_description),
                    Toast.LENGTH_LONG)
                toast.show()

                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.sendNotification(
                    getString(R.string.notification_description),
                    applicationContext,
                    selectedNameDownload!!,
                    downloadStatus,
                    CHANNEL_ID
                )
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(linkTOBeDownloaded))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }


    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val Retrofit_url = "https://github.com/square/retrofit/archive/master.zip"
        private const val Glide_url = "https://github.com/bumptech/glide/archive/master.zip"
    }

    fun onSelectedRadioButton(view: android.view.View) {
        if (view is RadioButton && view.isChecked) {

            when (view.getId()) {
                R.id.rd_glide ->
                    linkTOBeDownloaded = Glide_url
                R.id.rd_load ->
                    linkTOBeDownloaded = URL
                R.id.rd_retrofit ->
                    linkTOBeDownloaded = Retrofit_url
            }

            selectedNameDownload = view.text.toString()
        }

    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}
