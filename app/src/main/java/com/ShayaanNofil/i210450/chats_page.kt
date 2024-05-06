package com.ShayaanNofil.i210450

import Chats
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chatsearch_recycle_adapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var mAuth: FirebaseAuth
private lateinit var typeofuser: String
private lateinit var userchats : MutableList<String>
class chats_page : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats_page)
        mAuth = Firebase.auth

        val recycle_chat: RecyclerView = findViewById(R.id.chatpage_recycler_view)
        recycle_chat.layoutManager = LinearLayoutManager(this@chats_page)
        var chatidarray = mutableListOf<String>()

        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).child("Chats").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(String::class.java)
                        if (myclass != null) {
                            chatidarray.add(myclass)
                        }
                    }
                    FirebaseDatabase.getInstance().getReference("Chats").addListenerForSingleValueEvent(object:
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val chatarray: MutableList<Chats> = mutableListOf()
                            if (snapshot.exists()){
                                for (data in snapshot.children){
                                    val myclass = data.getValue(Chats::class.java)
                                    if (myclass != null) {
                                        if (chatidarray.contains(myclass.id)){
                                            chatarray.add(myclass)
                                        }
                                    }
                                }
                                val adapter = chatsearch_recycle_adapter(chatarray)
                                recycle_chat.adapter = adapter

                                adapter.setOnClickListener(object :
                                    chatsearch_recycle_adapter.OnClickListener {
                                    override fun onClick(position: Int, model: Chats) {
                                        val intent = Intent(this@chats_page, individual_chat::class.java)
                                        intent.putExtra("object", model)
                                        startActivity(intent)
                                    }
                                })
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.w("TAG", "Failed to read value.", error.toException())
                        }
                    })

                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        val backbutton = findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val task_homebutton = findViewById<View>(R.id.bthome)
        task_homebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, home_page::class.java)
            startActivity(temp)
        })

        val task_searchbutton = findViewById<View>(R.id.btsearch)
        task_searchbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, Search::class.java)
            startActivity(temp)
        })

        val task_chatbutton = findViewById<View>(R.id.btchat)
        task_chatbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, chats_page::class.java)
            startActivity(temp)
        })

        val task_profilebutton = findViewById<View>(R.id.btprofile)
        task_profilebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, profile_page::class.java)
            startActivity(temp)
        })

        val task_addcontent = findViewById<View>(R.id.btaddcontent)
        task_addcontent.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, addnew_post::class.java)
            startActivity(temp)
        })
    }

    fun setrecyclerdata(){
        FirebaseDatabase.getInstance().getReference(typeofuser).child(mAuth.uid!!).child("Chats").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userchats = mutableListOf()
                for (data in snapshot.children){
                    userchats.add(data.getValue(String::class.java).toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }
}
