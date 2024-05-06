package com.ShayaanNofil.i210450

import Posts
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

private lateinit var database: DatabaseReference
private lateinit var mAuth: FirebaseAuth
class home_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        mAuth = Firebase.auth

        val recycle_topmentor: RecyclerView = findViewById(R.id.recycle_posts)
        recycle_topmentor.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, true)
        val postarray: MutableList<Posts> = mutableListOf()
        val useridarray: MutableList<String> = mutableListOf()

        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).child("Friends").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                useridarray.clear()
                useridarray.add(mAuth.uid.toString())
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(String::class.java)
                        if (myclass != null) {
                            useridarray.add(myclass)
                        }
                    }

                    FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(object:
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            postarray.clear()
                            if (snapshot.exists()){
                                for (data in snapshot.children){
                                    val myclass = data.getValue(Posts::class.java)
                                    if (myclass != null) {
                                        if (useridarray.contains(myclass.senderid)){
                                            postarray.add(myclass)
                                        }
                                    }
                                }

                                val adapter = postrecycle_adapter(postarray)
                                recycle_topmentor.adapter = adapter
                                adapter.setOnClickListener(object :
                                    postrecycle_adapter.OnClickListener {
                                    override fun onClick(position: Int, model: Posts) {
                                        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                                        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

                                        val temp = Intent(this@home_page, post_comments::class.java )
                                        temp.putExtra("object", model)
                                        startActivity(temp)
                                    }
                                })
                                adapter.setOnAcceptClickListener(object :
                                    postrecycle_adapter.OnAcceptClickListener {
                                    override fun onAcceptClick(position: Int, model: Posts) {
                                        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                                        // For newer APIs
                                        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                                        FirebaseDatabase.getInstance().getReference("Posts").child(model.id).child("Likes").addListenerForSingleValueEvent(object:
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
                                                        model.likes -= 1
                                                        FirebaseDatabase.getInstance().getReference("Posts").child(model.id).child("Likes").child(Firebase.auth.uid.toString()).removeValue()
                                                        FirebaseDatabase.getInstance().getReference("Posts").child(model.id).child("likes").setValue(model.likes)
                                                    }
                                                    else{
                                                        model.likes += 1
                                                        FirebaseDatabase.getInstance().getReference("Posts").child(model.id).child("Likes").child(Firebase.auth.uid.toString()).setValue(Firebase.auth.uid.toString())
                                                        FirebaseDatabase.getInstance().getReference("Posts").child(model.id).child("likes").setValue(model.likes)
                                                    }
                                                }
                                                else{
                                                    Log.w("TAG", "liked")
                                                    model.likes += 1
                                                    FirebaseDatabase.getInstance().getReference("Posts").child(model.id).child("Likes").child(Firebase.auth.uid.toString()).setValue(Firebase.auth.uid.toString())
                                                    FirebaseDatabase.getInstance().getReference("Posts").child(model.id).child("likes").setValue(model.likes)
                                                }
                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }
                                        })
                                    }
                                })

                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        FirebaseDatabase.getInstance().getReference("Requests").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    var exists = false
                    for (data in snapshot.children){
                        val myclass = data.getValue(Requests::class.java)
                        if (myclass != null) {
                            if (myclass.recieverid == mAuth.uid.toString()){
                                exists = true
                            }
                        }
                    }
                    if (exists){
                        var notif = findViewById<CircleImageView>(R.id.notif_button)
                        notif.setImageResource(R.drawable.gold_notification)
                    }
                    else{
                        var notif = findViewById<CircleImageView>(R.id.notif_button)
                        notif.setBackgroundResource(R.drawable.notif_icon)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        val notifbutton=findViewById<View>(R.id.notif_button)
        notifbutton.setOnClickListener(View.OnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            val temp = Intent(this, notifications::class.java )
            startActivity(temp)
        })

        val task_homebutton=findViewById<View>(R.id.bthome)
        task_homebutton.setOnClickListener(View.OnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            val temp = Intent(this, home_page::class.java )
            startActivity(temp)
        })

        val task_searchbutton=findViewById<View>(R.id.btsearch)
        task_searchbutton.setOnClickListener(View.OnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            val temp = Intent(this, Search::class.java )
            startActivity(temp)
        })

        val task_chatbutton=findViewById<View>(R.id.btchat)
        task_chatbutton.setOnClickListener(View.OnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            val temp = Intent(this, chats_page::class.java )
            startActivity(temp)
        })

        val task_profilebutton=findViewById<View>(R.id.btprofile)
        task_profilebutton.setOnClickListener(View.OnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            val temp = Intent(this, profile_page::class.java )
            startActivity(temp)
        })

        val task_addcontent=findViewById<View>(R.id.btaddcontent)
        task_addcontent.setOnClickListener(View.OnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            val temp = Intent(this, addnew_post::class.java )
            startActivity(temp)
        })
    }
}
