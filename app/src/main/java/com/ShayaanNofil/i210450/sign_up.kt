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
import android.widget.Spinner
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
private lateinit var database: DatabaseReference
class sign_up : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var name: EditText
    lateinit var number: EditText
    lateinit var city: EditText
    lateinit var  country: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mAuth = Firebase.auth
        val loginbutton=findViewById<View>(R.id.login)
        loginbutton.setOnClickListener(View.OnClickListener {
            val login = Intent(this, Log_in_page::class.java )
            startActivity(login)
            finish()
        })

        val signupbutton=findViewById<View>(R.id.signup_button)
        signupbutton.setOnClickListener(View.OnClickListener {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))

            name = findViewById(R.id.name_box)
            email = findViewById(R.id.email_box)
            password = findViewById(R.id.password_box)
            number = findViewById(R.id.contact_box)
            city = findViewById(R.id.city_box)
            country = findViewById(R.id.country_box)

            if (email.text.isNotEmpty() && password.text.length >= 6 && name.text.isNotEmpty() && number.text.isNotEmpty() && city.text.isNotEmpty() && country.selectedItem.toString().isNotEmpty()){
                signup(email.text.toString(), password.text.toString())
            }

        })
    }
    fun signup(email:String,pass:String){
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "createUserWithEmail:success")

                    database = FirebaseDatabase.getInstance().getReference("User")
                    var usr: User = User()
                    var userId = mAuth.uid;
                    usr.addData(userId.toString(), name.text.toString(), email, number.text.toString(), city.text.toString(), country.selectedItem.toString())
                    usr.id = mAuth.uid.toString()

                    database.child(userId!!).setValue(usr).addOnCompleteListener {

                        FirebaseApp.initializeApp(this)
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
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
                                                .child(Firebase.auth.uid.toString()).setValue(usr)
                                        })

                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {}
                            })
                        })

                        var secondActivityIntent = Intent(this, profile_page::class.java)
                        startActivity(secondActivityIntent)
                        finish()
                    }.addOnFailureListener{
                        Log.w("TAG", "Didnt Register", task.exception)
                    }

                } else {
// If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    val text = "Didnt work"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this, text, duration) // in Activity
                    toast.show()
                }
            }
    }
}