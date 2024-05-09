package com.ShayaanNofil.i210450

import User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

private lateinit var mAuth: FirebaseAuth
class Log_in_page : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mAuth = Firebase.auth
        // Check if user is already logged in
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirebaseApp.initializeApp(this)
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                val token = task.result
                Log.d("MyToken", token)

                FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object:
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Log.w("TAG", "User is a user")
                            var usr : User = snapshot.getValue(User::class.java)!!
                            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                                    return@OnCompleteListener
                                }
                                // Get new FCM registration token
                                val token = task.result
                                Log.d("MyToken", token)
                                usr.token = token
                                FirebaseDatabase.getInstance().getReference("User")
                                    .child(Firebase.auth.uid.toString())
                                    .child("token")
                                    .setValue(token)
                            })
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            })
            // User is already logged in, navigate to home page
            val secondActivityIntent = Intent(this, home_page::class.java)
            startActivity(secondActivityIntent)
            finish()
            return
        }


        val loginbutton=findViewById<View>(R.id.login_button)
        loginbutton.setOnClickListener(View.OnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            email = findViewById<EditText>(R.id.email_box)
            password = findViewById<EditText>(R.id.password_box)

            if (email.text.toString() == ""){
                email.setText(" ")
            }
            if (password.text.toString() == ""){
                password.setText(" ")
            }

            signin(email.text.toString(), password.text.toString())
        })

        val signupbutton=findViewById<View>(R.id.signup)
        signupbutton.setOnClickListener(View.OnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            val signin = Intent(this, sign_up::class.java )
            startActivity(signin)
        })

        val newpassword=findViewById<View>(R.id.forgot_password)
        newpassword.setOnClickListener(View.OnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            val changepassword = Intent(this, forgot_password_email::class.java )
            startActivity(changepassword)
        })
    }
    fun signin(email:String,pass:String){
        var mAuth: FirebaseAuth = Firebase.auth
        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    FirebaseApp.initializeApp(this)
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new FCM registration token
                        val token = task.result
                        Log.d("MyToken", token)

                        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object:
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    Log.w("TAG", "User is a user")
                                    var usr : User = snapshot.getValue(User::class.java)!!
                                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                        if (!task.isSuccessful) {
                                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                                            return@OnCompleteListener
                                        }
                                        // Get new FCM registration token
                                        val token = task.result
                                        Log.d("MyToken", token)
                                        usr.token = token
                                        FirebaseDatabase.getInstance().getReference("User")
                                            .child(Firebase.auth.uid.toString())
                                            .child("token")
                                            .setValue(token)
                                    })

                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    })

                    var secondActivityIntent = Intent(this, home_page::class.java)
                    startActivity(secondActivityIntent)
                    finish()
                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    val text = "Wrong Credentials"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this, text, duration) // in Activity
                    toast.show()
                }
            }
        }
    }