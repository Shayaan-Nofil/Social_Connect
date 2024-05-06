package com.ShayaanNofil.i210450

import Chats
import Posts
import User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
private lateinit var mentor: User
private lateinit var username: String
class profile_viewer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_)

        mentor = intent.getSerializableExtra("object") as User
        val mentorname: TextView = findViewById(R.id.name_text)
        mentorname.text = mentor.name

        val mentorimg: CircleImageView = findViewById(R.id.johnimg)
        Glide.with(this).load(mentor.profilepic).into(mentorimg)

        mAuth = Firebase.auth
        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val myclass = snapshot.getValue(User::class.java)
                    if (myclass != null){
                        username = myclass.name
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).child("Friends").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var exists = false
                    for (data in snapshot.children){
                        val myclass = data.getValue(String::class.java)
                        if (myclass == mentor.id){
                            exists = true
                        }
                    }
                    if (exists == true){
                        val bookbutton: Button = findViewById(R.id.add_friend)
                        bookbutton.text = "Friend"
                        bookbutton.visibility = View.GONE
                        displayposts(mentor)
                    }
                    else{
                        val messagebt: Button = findViewById(R.id.message_button)
                        messagebt.visibility = View.GONE
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val bookbutton=findViewById<View>(R.id.add_friend)
        bookbutton.setOnClickListener(View.OnClickListener {

            var req = Requests()
            req.id = FirebaseDatabase.getInstance().getReference("Requests").push().key.toString()
            req.senderid = mAuth.uid.toString()
            req.recieverid = mentor.id

            database = FirebaseDatabase.getInstance().getReference("Requests")
            database.child(req.id).setValue(req).addOnCompleteListener {
                Log.w("TAG", "Friend request sent")
                sendNotiftoRecepients(req)
            }.addOnFailureListener{
                Log.w("TAG", "Friend request not sent")
            }

        })

        val messagebutton=findViewById<View>(R.id.message_button)
        messagebutton.setOnClickListener(View.OnClickListener {
            var chat : Chats = Chats()
            var exists : Boolean = false

            var userchatarray: MutableList<String> = mutableListOf()
            FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).child("Chats").addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (data in snapshot.children){
                            val myclass = data.getValue(String::class.java)
                            if (myclass != null){
                                userchatarray.add(myclass)
                            }
                        }
                        for (chatid in userchatarray){
                            FirebaseDatabase.getInstance().getReference("User").child(mentor.id).child("Chats").addValueEventListener(object: ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()){
                                        for (data in snapshot.children){
                                            val myclass = data.getValue(String::class.java)
                                            if (myclass != null){
                                                if (myclass == chatid){
                                                    FirebaseDatabase.getInstance().getReference("Chats").child(chatid).addValueEventListener(object: ValueEventListener{
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            if (snapshot.exists()){
                                                                for (data in snapshot.children){
                                                                    val myclass = data.getValue(Chats::class.java)
                                                                    if (myclass != null){
                                                                        val temp = Intent(this@profile_viewer, individual_chat::class.java )
                                                                        temp.putExtra("object", myclass)
                                                                        startActivity(temp)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        override fun onCancelled(error: DatabaseError) {
                                                            Log.w("TAG", "Failed to read value.", error.toException())
                                                        }
                                                    })
                                                }
                                            }
                                        }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Log.w("TAG", "Failed to read value.", error.toException())
                                }
                            })
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w("TAG", "Failed to read value.", error.toException())
                }
            })

        })
    }

    private fun displayposts(mentor: User){
        val recycle_topmentor: RecyclerView = findViewById(R.id.recycle_top_mentors)
        recycle_topmentor.layoutManager = GridLayoutManager(this, 2)
        val postarray: MutableList<Posts> = mutableListOf()


        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postarray.clear()
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(Posts::class.java)
                        if (myclass!!.senderid == mentor.id){
                            postarray.add(myclass)
                        }
                    }

                    val adapter = postrecycle_small_adapter(postarray)
                    recycle_topmentor.adapter = adapter

                    adapter.setOnClickListener(object :
                        postrecycle_small_adapter.OnClickListener {
                        override fun onClick(position: Int, model: Posts) {
                            val temp = Intent(this@profile_viewer, post_viewer::class.java )
                            temp.putExtra("object", mentor.id)
                            startActivity(temp)
                        }
                    })

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun sendPushNotification(token: String, title: String, subtitle: String, body: String, data: Map<String, String> = emptyMap()) {
        val url = "https://fcm.googleapis.com/fcm/send"
        val bodyJson = JSONObject()
        bodyJson.put("to", token)
        bodyJson.put("notification",
            JSONObject().also {
                it.put("title", title)
                it.put("subtitle", subtitle)
                it.put("body", body)
                it.put("sound", "social_notification_sound.wav")
            }
        )
        Log.d("TAG", "sendPushNotification: ${JSONObject(data)}")
        if (data.isNotEmpty()) {
            bodyJson.put("data", JSONObject(data))
        }

        val key="AAAAb6fXF78:APA91bFKixX-2V-65t9kATt0z2AHiiOW4EegsUoD22gKGt9X6eOT7eelzfsRftGLLSuWv-NLyVeCovg4rOqNWxryQWa4kVuDgL967m11N2iaxHGntZ8AeDORae6K61r22J7FilhmO1QI"
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "key=$key")
            .post(
                bodyJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            )
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    println("Received data: ${response.body?.string()}")
                    Log.d("TAG", "onResponse: ${response}   ")
                    Log.d("TAG", "onResponse Message: ${response.message}   ")
                }

                override fun onFailure(call: Call, e: IOException) {
                    println(e.message.toString())
                    Log.d("TAG", "onFailure: ${e.message.toString()}")
                }
            }
        )
    }

    fun sendNotiftoRecepients(req: Requests){
        sendPushNotification(mentor.token, "New Friend Request from " + username, "", "", mapOf("requestid" to req.id))
    }
}