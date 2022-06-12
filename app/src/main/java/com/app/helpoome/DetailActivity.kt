package com.app.helpoome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        val reqButton = findViewById<Button>(R.id.reqButton)

        var name = findViewById<TextView>(R.id.tolName)
        var address = findViewById<TextView>(R.id.tolLocation)

        name.text = intent.getStringExtra("dataName")
        address.text = intent.getStringExtra("dataAddress")

        reqButton.setOnClickListener {
            val intent = Intent(this, RequestActivity::class.java)
            startActivity(intent)
        }
    }
}