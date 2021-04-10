package com.darknamer.camera_permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : AppCompatActivity() {
    private val REQ_CODE_CAMERA = 555
    private val REQ_IMAGE_CAPTURE = 500

    private val CAMERA = Manifest.permission.CAMERA
    private val GRANTED = PackageManager.PERMISSION_GRANTED

    private val FILE_NAME = "photos_"

    private var photoFile: File? = null
    private var buttonTakePhoto: Button? = null
    private var imageViewPreview: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check your application is allow
        if (ContextCompat.checkSelfPermission(baseContext, CAMERA) != GRANTED) {
            requestPermissions(arrayOf(CAMERA), REQ_CODE_CAMERA)
        }

        buttonTakePhoto = findViewById(R.id.buttonTakePhoto)
        imageViewPreview = findViewById(R.id.imageViewPreview)

        buttonTakePhoto?.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            // This DOESN'T work for API >= 24 (starting 2016)
            // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)

            val fileProvider = FileProvider.getUriForFile(
                this,
                "com.darknamer.camera_permission.fileprovider",
                photoFile!!
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, REQ_IMAGE_CAPTURE)
            } else {
                Toast.makeText(this, "Unable to open camera.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_CODE_CAMERA &&
            permissions.isNotEmpty() &&
            grantResults[0] == GRANTED
        ) {
            Toast.makeText(baseContext, "Camera is allow.", Toast.LENGTH_LONG).show()
        } else {
            // Can't to use application because no allow camera
            finishAffinity();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            val takenImage = data?.extras?.get("data") as Bitmap
            val takenImage = BitmapFactory.decodeFile(photoFile?.absolutePath)
            imageViewPreview?.setImageBitmap(takenImage)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}