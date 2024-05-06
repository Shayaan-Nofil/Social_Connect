package com.ShayaanNofil.i210450

import User
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView


private lateinit var storage: FirebaseStorage
private lateinit var database: DatabaseReference
private lateinit var mAuth: FirebaseAuth

class edit_profile : AppCompatActivity() {
    private var profilePictureUri: Uri? = null
    lateinit var profilepicimg: CircleImageView
    lateinit var usr: User
    lateinit var namebox: EditText
    lateinit var emailbox: EditText
    lateinit var countrybox: Spinner
    lateinit var citybox: EditText
    lateinit var numbox: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        mAuth = Firebase.auth
        var userId = mAuth.uid;
        usr = User()
        usr.id = userId!!
        database = FirebaseDatabase.getInstance().getReference("User").child(userId)
        profilepicimg = findViewById(R.id.profile_pic)
        namebox = findViewById(R.id.name_box)
        emailbox = findViewById(R.id.email_box)
        citybox = findViewById(R.id.city_box)
        numbox = findViewById(R.id.contact_box)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //usr.id = snapshot.child("id").value.toString()
                usr.name = snapshot.child("name").value.toString()
                usr.email = snapshot.child("email").value.toString()
                usr.country = snapshot.child("country").value.toString()
                usr.city = snapshot.child("city").value.toString()
                usr.number = snapshot.child("number").value.toString()
                usr.profilepic = snapshot.child("profilepic").value.toString()
                usr.bgpic = snapshot.child("bgpic").value.toString()
                namebox.hint = usr.name
                emailbox.hint = usr.email
                citybox.hint = usr.city
                numbox.hint = usr.number
                updateimg()
            }
            override fun onCancelled(error: DatabaseError){}
        })


        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        namebox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                usr.name = namebox.text.toString()
            }
        })

        numbox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                usr.number = numbox.text.toString()
            }
        })

        citybox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                usr.city = citybox.text.toString()
            }
        })


        val updatebutton=findViewById<View>(R.id.update_button)
        updatebutton.setOnClickListener(View.OnClickListener {

            database.setValue(usr).addOnSuccessListener {
                profilepicimg.setImageURI(profilePictureUri)
                finish()
            }
        })

    }
    fun updateimg(){
        if (usr.profilepic.isNotEmpty()){
            Glide.with(this)
                .load(usr.profilepic)
                .into(profilepicimg)
        }
    }

}