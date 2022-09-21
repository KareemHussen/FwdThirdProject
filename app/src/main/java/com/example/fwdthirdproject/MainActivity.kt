package com.example.fwdthirdproject

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private  var theLink : String? = null
    private  var selectedNameDownload : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(broadCastReciver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadting_button.setOnClickListener {
            if(theLink != null) {
                loadting_button.buttonState = ButtonState.Loading
                download()
            }
        }

        createChannel(CHANNEL_ID,getString(R.string.notification_title))
    }

    private val broadCastReciver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            loadting_button.buttonState = ButtonState.Completed

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val query = DownloadManager.Query()
            query.setFilterById(id!!)

            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                val navigatorIntent = Intent(this@MainActivity , DetailActivity::class.java)

                var downloadStatus = "Fail"

                if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    downloadStatus = "Success"
                }
                navigatorIntent.putExtra("status", downloadStatus)
                navigatorIntent.putExtra("filename" ,selectedNameDownload )

                startActivity(navigatorIntent)
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
            DownloadManager.Request(Uri.parse(theLink))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    fun onSelectedRadioButton(view: android.view.View) {
        if (view is RadioButton && view.isChecked) {

            when (view.getId()) {
                R.id.rd_glide ->
                    theLink = Glide_url
                R.id.rd_load ->
                    theLink = URL
                R.id.rd_retrofit ->
                    theLink = Retrofit_url
            }

            selectedNameDownload = view.text.toString()
        }

    }

    private fun createChannel(channel: String, channelName: String) {
        val notificationChannel = NotificationChannel(
            channel,
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

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val Retrofit_url = "https://github.com/square/retrofit/archive/master.zip"
        private const val Glide_url = "https://github.com/bumptech/glide/archive/master.zip"
    }

}
