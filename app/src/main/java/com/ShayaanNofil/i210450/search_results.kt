package com.ShayaanNofil.i210450

import User
import android.content.Intent
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import searchrecycle_adapter

private lateinit var mAuth: FirebaseAuth
class search_results : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        mAuth = Firebase.auth

        val intent = intent
        val mentorname = intent.getStringExtra("mentor").toString()

        val recycle_topmentor: RecyclerView = findViewById(R.id.searchresults_recycle)
        recycle_topmentor.layoutManager = LinearLayoutManager(this)

        val userarray: MutableList<User> = mutableListOf()

        FirebaseDatabase.getInstance().getReference("User").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userarray.clear()
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(User::class.java)
                        if (myclass != null) {
                            if (mentorname.isNotEmpty()){
                                if (myclass.name.toLowerCase().contains(mentorname.toLowerCase()) && myclass.id != mAuth.uid.toString()){
                                    userarray.add(myclass)
                                }
                            }
                            else{
                                if (myclass.id != mAuth.uid.toString()){
                                    userarray.add(myclass)
                                }
                            }

                        }
                    }
                    Log.w("TAG", "Display recycle")
                    val adapter = searchrecycle_adapter(userarray)
                    recycle_topmentor.adapter = adapter

                    adapter.setOnClickListener(object :
                        searchrecycle_adapter.OnClickListener {
                        override fun onClick(position: Int, model: User) {
                            val intent = Intent(this@search_results, profile_viewer::class.java)
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

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            finish()
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