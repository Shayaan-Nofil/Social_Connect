package com.ShayaanNofil.i210450

import Chats
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import java.util.*
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import java.lang.Exception


private lateinit var chat: Chats
private lateinit var recieverid: String
private lateinit var revieverimg: String
private lateinit var recievername: String
private lateinit var mAuth: FirebaseAuth
private lateinit var mRtcEngine: RtcEngine
private var callStartedTimeMillis = 0L


class voice_call : AppCompatActivity() {
    private var calltime: TextView? = null
    private val APP_ID = "def8448e122f467e86b87e4491d50c64"
    // Fill the channel name.
    private val CHANNEL = "SMD_A2"
    // Fill the temp token generated on Agora Console.
    private val TOKEN = "007eJxTYHi84sOKkEa3yvZdyqxXJQ0Y42fru9Va5r9pbd0ve7yM7bICQ0pqmoWJiUWqoZFRmomZeaqFWZKFeaqJiaVhiqlBspmJkvH/1IZARoaHpveYGRkgEMRnYwj2dYl3NGJgAABDQh6J"
    private var mRtcEngine: RtcEngine ?= null
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
    }


    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(permission),
                requestCode)
            return false
        }
        return true
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_call)
        calltime = findViewById(R.id.calltime)

        val bundle = intent.extras
        if (bundle != null){
            chat = bundle.getSerializable("chatdata", Chats::class.java)!!
            //revieverimg = bundle.getString("recieverimg")!!
            //recievername = bundle.getString("recievername")!!
        }

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initializeAndJoinChannel();
        }

        val endbutton=findViewById<View>(R.id.end_button)
        endbutton.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }

    private fun initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, APP_ID, mRtcEventHandler)
        } catch (e: Exception) {
        }
        mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0)
    }


    private fun updateCallTime() {
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val callDuration = System.currentTimeMillis() - callStartedTimeMillis
                    val seconds = (callDuration / 1000) % 60
                    val minutes = (callDuration / (1000 * 60)) % 60
                    val hours = (callDuration / (1000 * 60 * 60)) % 24

                    calltime!!.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                }
            }
        }, 0, 1000)
    }
}