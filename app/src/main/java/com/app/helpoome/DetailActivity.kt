package com.app.helpoome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        val reqButton = findViewById<Button>(R.id.reqButton)

        var nameText = findViewById<TextView>(R.id.nameText)
        var tolName = findViewById<TextView>(R.id.tolName)
        var tolAddress = findViewById<TextView>(R.id.tolAddress)
        var tolTime = findViewById<TextView>(R.id.tolTime)
        var tolWc = findViewById<TextView>(R.id.tolWc)
        var tolDp = findViewById<TextView>(R.id.tolDp)
        var tolCall = findViewById<TextView>(R.id.tolCall)
        var tolDiv = findViewById<TextView>(R.id.tolDiv)

        nameText.text = MainActivity.name
        tolName.text = MainActivity.name
        tolAddress.text = MainActivity.address
        tolTime.text = MainActivity.time
        tolWc.text = MainActivity.wc
        tolDp.text = MainActivity.dp
        tolCall.text = MainActivity.call
        tolDiv.text = MainActivity.divTol

//        reqButton.setOnClickListener {
//            val intent = Intent(this, RequestActivity::class.java)
//            startActivity(intent)
//        }
    }
}