package com.ShayaanNofil.i210450

import Chats
import User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import requests_recycle_adapter

private lateinit var mAuth: FirebaseAuth
class notifications : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        mAuth = Firebase.auth

        val recycle_topmentor: RecyclerView = findViewById(R.id.friend_requests)
        recycle_topmentor.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val requesarray: MutableList<Requests> = mutableListOf()
        val userarray: MutableList<User> = mutableListOf()

        FirebaseDatabase.getInstance().getReference("Requests").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                requesarray.clear()
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(Requests::class.java)
                        if (myclass != null) {
                            if (myclass.recieverid == mAuth.uid.toString()){
                                requesarray.add(myclass)
                                Log.w("TAG", "Added Requests")
                            }
                            Log.w("TAG", "Found Requests")
                        }
                    }

                    FirebaseDatabase.getInstance().getReference("User").addValueEventListener(object:
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            userarray.clear()
                            if (snapshot.exists()){
                                for (data in snapshot.children){
                                    val myclass = data.getValue(User::class.java)
                                    if (myclass != null) {
                                        for (request in requesarray){
                                            if (request.senderid == myclass.id){
                                                userarray.add(myclass)
                                            }
                                        }
                                    }
                                }

                                val adapter = requests_recycle_adapter(userarray)
                                recycle_topmentor.adapter = adapter
                                adapter.setOnAcceptClickListener(object :
                                    requests_recycle_adapter.OnAcceptClickListener {
                                    override fun onAcceptClick(position: Int, model: User) {
                                            FirebaseDatabase.getInstance().getReference("Requests").addValueEventListener(object:
                                            ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()){
                                                    for (data in snapshot.children){
                                                        val myclass = data.getValue(Requests::class.java)
                                                        if (myclass != null) {
                                                            if (myclass.senderid == model.id && myclass.recieverid == mAuth.uid.toString()){
                                                                var chat = Chats()
                                                                chat.id = FirebaseDatabase.getInstance().getReference("Chats").push().key.toString()
                                                                FirebaseDatabase.getInstance().getReference("Chats").child(chat.id).setValue(chat)

                                                                FirebaseDatabase.getInstance().getReference("Requests").child(data.key.toString()).removeValue()
                                                                FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).child("Friends").push().setValue(model.id)
                                                                FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).child("Chats").child(chat.id).setValue(chat.id)
                                                                FirebaseDatabase.getInstance().getReference("User").child(model.id).child("Friends").push().setValue(mAuth.uid.toString())
                                                                FirebaseDatabase.getInstance().getReference("User").child(model.id).child("Chats").push().setValue(chat.id)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }
                                        })
                                    }
                                })

                                adapter.setOnRejectClickListener(object :
                                    requests_recycle_adapter.OnRejectClickListener {
                                    override fun onRejectClick(position: Int, model: User) {
                                        FirebaseDatabase.getInstance().getReference("Requests").addValueEventListener(object:
                                            ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()){
                                                    for (data in snapshot.children){
                                                        val myclass = data.getValue(Requests::class.java)
                                                        if (myclass != null) {
                                                            if (myclass.senderid == model.id && myclass.recieverid == mAuth.uid.toString()){
                                                                FirebaseDatabase.getInstance().getReference("Requests").child(data.key.toString()).removeValue()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }
                                        })
                                    }
                                })

                                adapter.setOnItemClickListener(object :
                                    requests_recycle_adapter.OnItemClickListener {
                                    override fun onItemClick(position: Int, model: User) {
                                        val intent = Intent(this@notifications, profile_viewer::class.java)
                                        intent.putExtra("object", model)
                                        startActivity(intent)
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





        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}