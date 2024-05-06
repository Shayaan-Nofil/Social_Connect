package com.ShayaanNofil.i210450

import Posts
import User
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.random.Random

private lateinit var storage: FirebaseStorage
private lateinit var database: DatabaseReference
private lateinit var mAuth: FirebaseAuth
class addnew_post : AppCompatActivity() {
    private var mentorimgUri: Uri? = null
    lateinit var uppic: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnew_post)
        mAuth = Firebase.auth
        var post = Posts()

        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).get().addOnSuccessListener {
            post.sendername = it.child("name").value.toString()
            post.senderimg = it.child("profilepic").value.toString()
        }

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val uploadbutton=findViewById<View>(R.id.upload_button)
        uploadbutton.setOnClickListener(View.OnClickListener {
            val postdisc : EditText = findViewById(R.id.desc_box)
            post.description = postdisc.text.toString()
            post.senderid = mAuth.uid.toString()

            if (mentorimgUri == null){
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            storage = FirebaseStorage.getInstance()
            val storageref = storage.reference
            database = FirebaseDatabase.getInstance().getReference("Posts")

            if (mentorimgUri != null){
                Log.w("TAG", "Uploading")
                storageref.child("Posts").child(post.senderid + Random.nextInt(0,100000).toString()).putFile(mentorimgUri!!).addOnSuccessListener {
                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {task ->
                        post.content = task.toString()
                        Log.w("TAG", "Upload Success")
                        post.id = database.push().key.toString()

                        database.child(post.id).setValue(post).addOnSuccessListener {
                            sendNotiftoRecepients(post)
                            finish()
                        }
                    }
                }.addOnFailureListener{
                    Log.w("TAG", "Upload failed")
                }
            }
            else{
                post.id = database.push().key.toString()
                database.child(post.id).setValue(post).addOnSuccessListener {
                    finish()
                }
            }
        })

        uppic= findViewById(R.id.up_pht)
        uppic.setOnClickListener(View.OnClickListener {
            openGalleryForImage()
        })

        val task_homebutton=findViewById<View>(R.id.bthome)
        task_homebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, home_page::class.java )
            startActivity(temp)
        })

        val task_searchbutton=findViewById<View>(R.id.btsearch)
        task_searchbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, Search::class.java )
            startActivity(temp)
        })

        val task_chatbutton=findViewById<View>(R.id.btchat)
        task_chatbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, chats_page::class.java )
            startActivity(temp)
        })

        val task_profilebutton=findViewById<View>(R.id.btprofile)
        task_profilebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, profile_page::class.java )
            startActivity(temp)
        })

        val task_addcontent=findViewById<View>(R.id.btaddcontent)
        task_addcontent.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, camera_picture_mode::class.java )
            startActivity(temp)
        })
    }
    private val galleryprofileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uppic.setImageURI(uri)
        if (uri != null){
            mentorimgUri = uri
        }
    }
    private fun openGalleryForImage() {
        galleryprofileLauncher.launch("image/*")
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

    fun sendNotiftoRecepients(post: Posts){
        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).child("Friends").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(String::class.java)
                        if (myclass != null) {
                            FirebaseDatabase.getInstance().getReference("User").child(myclass).get().addOnSuccessListener {
                                val usr = it.getValue(User::class.java)
                                if (usr != null){
                                    sendPushNotification(usr.token, post.sendername+ "Uploaded a new post ", "Description: ", post.description, mapOf("postid" to post.id))
                                }
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