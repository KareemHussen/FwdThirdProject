package com.example.fwdthirdproject

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()

        val fileName = intent.getStringExtra("filename")
        fileNameValue.text = fileName

        val status = intent.getStringExtra("status")


        if(status== "Success"){
            statusValue.setTextColor(Color.parseColor("#00FF00"))
        } else {
            statusValue.setTextColor(Color.parseColor("#FF0000"))
        }

        statusValue.text = status


        button.setOnClickListener {
            finish()
        }
    }

}