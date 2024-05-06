package com.ShayaanNofil.i210450

import Comments
import Posts
import User
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var mAuth: FirebaseAuth
class post_comments : AppCompatActivity() {
    private var post: Posts = Posts()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_comments)
        mAuth = Firebase.auth
        post = intent.getSerializableExtra("object") as Posts
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val mentorimg: ImageView = findViewById(R.id.mentor_img)
        var rqoptions: RequestOptions = RequestOptions()
        rqoptions= rqoptions.transform(CenterCrop(), RoundedCorners(40))
        Glide.with(this)
            .load(post.content)
            .apply(rqoptions)
            .into(mentorimg)

        findViewById<TextView>(R.id.mentor_name).text = post.sendername
        findViewById<TextView>(R.id.mentor_job).text = post.description

        val likebutton: Button = findViewById(R.id.like_button)
        FirebaseDatabase.getInstance().getReference("Posts").child(post.id).child("Likes").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var liked = false
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(String::class.java)
                        if (myclass != null) {
                            if (myclass == Firebase.auth.uid.toString()) {
                                liked = true
                                likebutton.setBackgroundResource(R.drawable.red_heart)
                            }
                        }
                    }
                    if (liked){
                        likebutton.setBackgroundResource(R.drawable.red_heart)
                        findViewById<TextView>(R.id.like_count).text = post.likes.toString()
                    }
                    else{
                        likebutton.setBackgroundResource(R.drawable.grey_heart)
                        findViewById<TextView>(R.id.like_count).text = post.likes.toString()
                    }
                }
                else{
                    likebutton.setBackgroundResource(R.drawable.grey_heart)
                    findViewById<TextView>(R.id.like_count).text = post.likes.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        val recycle_comments: RecyclerView = findViewById(R.id.post_comments)
        recycle_comments.layoutManager = LinearLayoutManager(this@post_comments, LinearLayoutManager.VERTICAL, false)
        val commentsarray: MutableList<Comments> = mutableListOf()

        FirebaseDatabase.getInstance().getReference("Posts").child(post.id).child("comments").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commentsarray.clear()
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(Comments::class.java)
                        if (myclass != null) {
                            commentsarray.add(myclass)
                        }
                    }
                    val adapter = comments_recycle_adapter(commentsarray)
                    recycle_comments.adapter = adapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        likebutton.setOnClickListener(){
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // For newer APIs
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            FirebaseDatabase.getInstance().getReference("Posts").child(post.id).child("Likes").addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var liked = false
                    if (snapshot.exists()){
                        for (data in snapshot.children){
                            val myclass = data.getValue(String::class.java)
                            if (myclass != null) {
                                if (myclass == Firebase.auth.uid.toString()) {
                                    liked = true
                                }
                            }
                        }
                        if (liked){
                            post.likes -= 1
                            FirebaseDatabase.getInstance().getReference("Posts").child(post.id).child("Likes").child(Firebase.auth.uid.toString()).removeValue()
                            FirebaseDatabase.getInstance().getReference("Posts").child(post.id).child("likes").setValue(post.likes)
                        }
                        else{
                            post.likes += 1
                            FirebaseDatabase.getInstance().getReference("Posts").child(post.id).child("Likes").child(Firebase.auth.uid.toString()).setValue(Firebase.auth.uid.toString())
                            FirebaseDatabase.getInstance().getReference("Posts").child(post.id).child("likes").setValue(post.likes)
                        }
                    }
                    else{
                        Log.w("TAG", "liked")
                        post.likes += 1
                        FirebaseDatabase.getInstance().getReference("Posts").child(post.id).child("Likes").child(Firebase.auth.uid.toString()).setValue(Firebase.auth.uid.toString())
                        FirebaseDatabase.getInstance().getReference("Posts").child(post.id).child("likes").setValue(post.likes)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }

        val sendbt: Button = findViewById(R.id.btsend)
        sendbt.setOnClickListener(){
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // For newer APIs
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            val comment = Comments()
            FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).addValueEventListener(object:
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val myclass = snapshot.getValue(User::class.java)
                        if (myclass != null) {
                            comment.sendername = myclass.name
                            comment.content = findViewById<EditText>(R.id.message_text).text.toString()
                            comment.senderid = mAuth.uid.toString()
                            if (comment.content != ""){
                                FirebaseDatabase.getInstance().getReference("Posts").child(post.id).child("comments").push().setValue(comment).addOnSuccessListener {
                                    findViewById<EditText>(R.id.message_text).setText("")
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
        val backbt: Button = findViewById(R.id.back_button)
        backbt.setOnClickListener(){
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // For newer APIs
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            finish()
        }
    }
}