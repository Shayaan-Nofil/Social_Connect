package com.ShayaanNofil.i210450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class verify_phone : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var verificationId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)

        //val phoneNumberEditText = findViewById<EditText>(R.id.idEdtPhoneNumber)
        val otpEditText = findViewById<EditText>(R.id.OtpText)
        val getOtpButton = findViewById<TextView>(R.id.sendagain_button)
        val verifyButton = findViewById<Button>(R.id.verify_button)

        mAuth = FirebaseAuth.getInstance()

        getOtpButton.setOnClickListener {
            //val phoneNumber = phoneNumberEditText.text.toString()
            val phoneNumber = "+923215564309"

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("TAG", "getOTP")
                val options = PhoneAuthOptions.newBuilder()
                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                    .setTimeout(110L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this)                 // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }

        verifyButton.setOnClickListener {
            val otp = otpEditText.text.toString()

            if (otp.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("TAG", "OTP: $otp")
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                signInWithPhoneAuthCredential(credential)
            }
        }

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    val user = task.result?.user
                    Toast.makeText(
                        this@verify_phone,
                        "Authentication successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, profile_page::class.java))
                    finish()
                    // You can navigate to the next activity or perform other operations here.
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        this@verify_phone,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            Log.d("TAG", "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {

            Log.d("TAG", "onCodeSent: $verificationId")
            this@verify_phone.verificationId = verificationId
            Toast.makeText(this@verify_phone, "OTP Sent", Toast.LENGTH_SHORT)
                .show()

        }
    }
}