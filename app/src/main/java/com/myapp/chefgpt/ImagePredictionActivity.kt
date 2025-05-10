package com.myapp.chefgpt

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.Manifest
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

class ImagePredictionActivity : AppCompatActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_imageprediction)
        val string = "zavagay"
        val takePictureButton = findViewById<Button>(R.id.openCamera)
        val pickImageButton = findViewById<Button>(R.id.openGallery)

        val buttonToRecipeResult: Button=findViewById(R.id.toRecipeResult)
        buttonToRecipeResult.setOnClickListener{ view->
            val intent= Intent(view.context,RecipeResultActivity::class.java)
            intent.putExtra("foodname",string)
            startActivity(intent)
        }

        val imageView = findViewById<ImageView>(R.id.imageView)

        var imageBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                launchCamera()
            } else {
                Log.e("PermissionDenied","Camera permission denied")
            }
        }

        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    imageView.setImageURI(imageUri)
                }else{
                    Log.e("CameraError","Image not saved")
                }
            }


        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val imageUri = result.data?.data
                    imageView.setImageURI(imageUri)
                }
            }


        takePictureButton.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        pickImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }


    }

    private fun  launchCamera(){
        val photoFile = File.createTempFile("photo",".jpg",cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        imageUri=FileProvider.getUriForFile(this,"${packageName}.provider",photoFile)
        takePictureLauncher.launch(imageUri)
    }

    private fun checkCameraPermissionAndLaunch() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            permissionLauncher.launch(permission)
        }
    }

}