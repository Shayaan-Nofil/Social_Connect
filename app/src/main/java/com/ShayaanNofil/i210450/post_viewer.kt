package com.ShayaanNofil.i210450

import Posts
import User
import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class post_viewer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_viewer)

        val mentor = intent.getSerializableExtra("object") as String

        val recycle_topmentor: RecyclerView = findViewById(R.id.post_recycler)
        recycle_topmentor.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val postarray: MutableList<Posts> = mutableListOf()

        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postarray.clear()
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(Posts::class.java)
                        if (myclass!!.senderid == mentor){
                            postarray.add(myclass)
                        }
                    }

                    val adapter = postrecycle_adapter(postarray)
                    recycle_topmentor.adapter = adapter

                    adapter.setOnClickListener(object :
                        postrecycle_adapter.OnClickListener {
                        override fun onClick(position: Int, model: Posts) {
                            val temp = Intent(this@post_viewer, post_comments::class.java )
                            temp.putExtra("object", model)
                            startActivity(temp)
                        }
                    })
                    adapter.setOnAcceptClickListener(object :
                        postrecycle_adapter.OnAcceptClickListener {
                        override fun onAcceptClick(position: Int, model: Posts) {
                            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
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


        findViewById<Button>(R.id.back_button).setOnClickListener(){
            finish()
        }
    }
}