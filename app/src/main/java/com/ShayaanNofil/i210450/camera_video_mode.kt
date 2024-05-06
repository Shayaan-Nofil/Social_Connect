package com.ShayaanNofil.i210450

import Chats
import Messages
import User
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

class camera_video_mode : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File
    private var chat: Chats? = Chats()
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var isrec: Boolean = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_video_mode)

        val bundle = intent.extras
        if (bundle != null){
            chat = bundle.getSerializable("chatdata", Chats::class.java)
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listener for take photo button
        val cameraCaptureButton: Button = findViewById(R.id.shutter_button)
        cameraCaptureButton.setOnClickListener {
            if (!isrec) {
                startRecording()
                cameraCaptureButton.setBackgroundResource(R.drawable.recording_icon_red)
            } else {
                cameraCaptureButton.setBackgroundResource(R.drawable.shutter_button_photo)
                stopRecording()
            }
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        var picbutton: Button = findViewById(R.id.picture_button)
        picbutton.setOnClickListener{
            val intent = Intent(this, camera_picture_mode::class.java)
            val bundle = Bundle()
            bundle.putSerializable("chatdata", chat)
            intent.putExtras(bundle)
            intent.putExtra("chatdata", chat)
            startActivity(intent)
            finish()
        }

    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(findViewById<PreviewView>(R.id.camera_preview).surfaceProvider)
                }

            // Initialize VideoCapture use case
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun startRecording() {
        val videoCapture = this.videoCapture ?: return

        isrec = true

        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(this@camera_video_mode,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)

                            // Call uploadVideoToFirestore function here
                            uploadVideoToFirestore(recordEvent.outputResults.outputUri)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                    }
                }
            }
    }

    private fun stopRecording() {
        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            isrec = false
        }
    }

    private fun uploadVideoToFirestore(uri: Uri) {
        Thread(Runnable {
            val message = Messages()
            val mAuth = Firebase.auth
            message.senderid = mAuth.uid.toString()
            message.time = Calendar.getInstance().time.toString()
            message.tag = "video"

            val storageref = FirebaseStorage.getInstance().reference

            storageref.child("Chats").child("Videos").child(chat!!.id + Random.nextInt(0,100000).toString()).putFile(uri).addOnSuccessListener {
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {task ->
                    message.content = task.toString()

                    FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object:
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val user = snapshot.getValue(User::class.java)
                                if (user != null) {
                                    message.senderpic = user.profilepic

                                    message.id = FirebaseDatabase.getInstance().getReference("Chats").child(chat!!.id).child("Messages").push().key.toString()
                                    FirebaseDatabase.getInstance().getReference("Chats").child(chat!!.id).child("Messages").child(message.id).setValue(message)
                                    finish()
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }.addOnFailureListener{
                Log.w("TAG", "Upload failed")
            }
        }).start()

        finish()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "camera_video_mode"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}