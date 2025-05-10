package com.myapp.chefgpt

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class ImagePredictionActivity : AppCompatActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_imageprediction)
        val string = "zavagay"
        val takePictureButton = findViewById<Button>(R.id.openCamera)
        val pickImageButton = findViewById<Button>(R.id.openGallery)

        val buttonToFoodResult: Button=findViewById(R.id.toRecipeResult)
        buttonToFoodResult.setOnClickListener{ view->
            val intent= Intent(view.context,RecipeResultActivity::class.java)
            intent.putExtra("foodname",string)
            startActivity(intent)

        }

        val imageView = findViewById<ImageView>(R.id.imageView)

        var imageBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)

        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imageBitmap = result.data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(imageBitmap)
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
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(intent)
        }

        pickImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }


    }

}