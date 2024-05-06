package com.ShayaanNofil.i210450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class forgot_password : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val loginbutton=findViewById<View>(R.id.login)
        loginbutton.setOnClickListener(View.OnClickListener {
            val login = Intent(this, Log_in_page::class.java )
            startActivity(login)
            finish()
        })

        val sendbutton=findViewById<View>(R.id.reset_button)
        sendbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, Log_in_page::class.java )
            startActivity(temp)
        })

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}