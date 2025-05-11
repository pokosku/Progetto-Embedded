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
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.myapp.chefgpt.ml.AutoModel1
import org.tensorflow.lite.support.image.TensorImage
import java.io.File


class ImagePredictionActivity : AppCompatActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var imageUri: Uri

    private lateinit var imageBitmap : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var loadImage: Boolean = false

        val model = AutoModel1.newInstance(this) //caricamento modello immagini

        setContentView(R.layout.activity_imageprediction)
        val takePictureButton = findViewById<Button>(R.id.openCamera)
        val pickImageButton = findViewById<Button>(R.id.openFavorites)
        val predictButton = findViewById<Button>(R.id.predictButton)
        val foodName= findViewById<TextView>(R.id.foodName)

        val imageView = findViewById<ImageView>(R.id.imageView)



        val buttonToRecipeResult: Button=findViewById(R.id.toRecipeResult)
        buttonToRecipeResult.setOnClickListener{ view->
            val intent= Intent(view.context,RecipeLoadingActivity::class.java)
            if(foodName.text!="Select an image first!!" && foodName.text!="Select an image" && foodName.text!="Press predict first!!"){
                try{
                    intent.putExtra("foodname",foodName.text)
                    intent.putExtra("foodimage",imageUri.toString())
                    startActivity(intent)}
                catch (e: UninitializedPropertyAccessException){
                    foodName.text="Select an image first!!"
                }
            }else{
                foodName.text="Press predict first!!"
            }
        }



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
                    loadImage=true
                }else{
                    Log.e("CameraError","Image not saved")
                }
            }


        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imageUri = result.data?.data!!
                    imageView.setImageURI(imageUri)
                    loadImage=true
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

        predictButton.setOnClickListener{
            if(loadImage) {
                foodName.text = imageClassification(imageView.drawable, model)
            }else{
                foodName.text="Select an image first!!"
            }
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

    private fun imageClassification(imageDrawable: Drawable, model: AutoModel1): String{
        try{
            val image = Bitmap.createBitmap((imageDrawable as BitmapDrawable).bitmap)
            imageBitmap = Bitmap.createScaledBitmap(image, 192, 192, true)
            val input = TensorImage.fromBitmap(imageBitmap)
            val outputs = model.process(input)
            val probability = outputs.probabilityAsCategoryList
            val best = (probability.maxByOrNull { it.score })!!.label
            return best
        }catch (e: NullPointerException){
            return "Select an image first"
        }
    }

}