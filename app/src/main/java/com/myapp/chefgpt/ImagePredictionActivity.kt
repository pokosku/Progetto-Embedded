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
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
    private var loadedImage : Boolean = false

    companion object {
        private const val KEY_IMAGE_URI = "key_image_uri"
        private const val KEY_FOOD_NAME_TEXT = "key_food_name_text"
        private const val KEY_LOADED_IMAGE = "loaded_image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imageprediction)

        val model = AutoModel1.newInstance(this) //caricamento modello immagini

        val takePictureButton = findViewById<Button>(R.id.openCamera)
        val pickImageButton = findViewById<Button>(R.id.openFavorites)
        val predictButton = findViewById<Button>(R.id.predictButton)
        val buttonToRecipeResult: Button=findViewById(R.id.toRecipeResult)
        val foodName= findViewById<TextView>(R.id.foodName)

        val imageView = findViewById<ImageView>(R.id.imageView)

        val toolbarView = findViewById<View>(R.id.toolbar)
        val backButton = toolbarView.findViewById<ImageButton>(R.id.back)
        val settingsButton = toolbarView.findViewById<ImageButton>(R.id.settings)

        if (savedInstanceState != null) {
            // Ripristina l'URI dell'immagine
            savedInstanceState.getString(KEY_IMAGE_URI)?.let {
                imageUri = Uri.parse(it)
                imageView.setImageURI(imageUri)
            }
            //Ripristina il boolean se l'immagine è già stata caricata oppure no
            loadedImage = savedInstanceState.getBoolean(KEY_LOADED_IMAGE)
            // Ripristina il testo della TextView
            foodName.text = savedInstanceState.getString(KEY_FOOD_NAME_TEXT)
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
                    loadedImage=true
                }else{
                    Log.e("CameraError","Image not saved")
                }
            }


        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imageUri = result.data?.data!!
                    imageView.setImageURI(imageUri)
                    loadedImage=true
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
            if(loadedImage) {
                foodName.text = imageClassification(imageView.drawable, model)
            }else{
                foodName.text="Select an image first!!"
            }
        }

        buttonToRecipeResult.setOnClickListener{ view->
            val intent= Intent(view.context,RecipeLoadingActivity::class.java)
            //TODO sistemare controlli stringhe perchè dopo aver aggiunto il recupero dal cambio di orientamento non fa andare avanti
            if(!loadedImage){
                foodName.text="Select an image first!!"
            }
            else{
                if(foodName.text!="Select an image first!!" && foodName.text!="Select an image" && foodName.text!="Press predict first!!"){
                    try{
                        intent.putExtra("foodname",foodName.text)
                        intent.putExtra("foodimage",imageUri.toString())
                        intent.putExtra("is_random_recipe",false)
                        startActivity(intent)}
                    catch (e: UninitializedPropertyAccessException){
                        foodName.text="Select an image first!!"
                    }
                }else{
                    foodName.text="Press predict first!!"
                }
            }
        }


        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        settingsButton.setOnClickListener{
            settingsButton.isEnabled = false
            val dialog = SettingsDialogFragment()
            dialog.onDismissListener = {
                settingsButton.isEnabled = true
            }
            dialog.show(supportFragmentManager, "settings_dialog")
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
    //TODO: testare rimozione try catch
    private fun imageClassification(imageDrawable: Drawable, model: AutoModel1): String{
        try{
            val image = Bitmap.createBitmap((imageDrawable as BitmapDrawable).bitmap)
            //TODO controllare eccezione bitmap troppo grande
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Salva l'URI dell'immagine se presente
        if (::imageUri.isInitialized) {
            outState.putString(KEY_IMAGE_URI, imageUri.toString())
        }
        //Salva il boolean se l'immagine è già stata caricata oppure no
        outState.putBoolean(KEY_LOADED_IMAGE,loadedImage)
        // Salva il testo della TextView
        val foodName = findViewById<TextView>(R.id.foodName)
        outState.putString(KEY_FOOD_NAME_TEXT, foodName.text.toString())
    }


}