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
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.myapp.chefgpt.ml.AutoModel1
import org.tensorflow.lite.support.image.TensorImage
import java.io.File
import java.util.Locale

//Activity per la predizione di immagini scattate o caricate da galleria

class ImagePredictionActivity : AppCompatActivity() {
    //variabili per la fotocamera
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var imageUri: Uri

    //variabili per passare le immagini al modello
    private lateinit var imageBitmap : Bitmap
    private var loadedImage : Boolean = false

    //Costanti varie (Intent, preferenze, ...)
    companion object {
        private const val KEY_LOADED_IMAGE = "loaded_image"
        private const val FOOD_IMAGE_URI_STRING_KEY = "food_image"
        private const val FOOD_NAME_KEY = "food_name"
        private const val IS_RANDOM_RECIPE_KEY = "is_random_recipe"
        private const val APP_PREFERENCES_KEY = "app_preferences"
        private const val THEME_KEY = "selected_theme"
        private const val LANGUAGE_KEY = "selected_language"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE)

        //Ottenimento della preferenza del tema
        val theme = preferences.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(theme)

        //Ottenimento della preferenza del linguaggio
        val languageCode = preferences.getString(LANGUAGE_KEY, "en") ?: "en"
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        this.resources.updateConfiguration(config, this.resources.displayMetrics)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imageprediction)

        //Caricamento modello di classificazione di immagini
        val model = AutoModel1.newInstance(this)

        //Settaggio dei bottoni, textview, imageview e toolbar
        val takePictureButton = findViewById<Button>(R.id.openCamera)
        val pickImageButton = findViewById<Button>(R.id.openFavorites)
        val predictButton = findViewById<Button>(R.id.predictButton)
        val buttonToRecipeResult = findViewById<Button>(R.id.toRecipeResult)
        val foodName= findViewById<TextView>(R.id.foodName)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val toolbarView = findViewById<View>(R.id.toolbar)
        val backButton = toolbarView.findViewById<ImageButton>(R.id.back)

        //Recupero dello stato salvato
        if (savedInstanceState != null) {
            // Ripristina l'URI dell'immagine
            savedInstanceState.getString(FOOD_IMAGE_URI_STRING_KEY)?.let {
                imageUri = Uri.parse(it)
                imageView.setImageURI(imageUri)
            }
            //Ripristina il boolean se l'immagine è già stata caricata oppure no
            loadedImage = savedInstanceState.getBoolean(KEY_LOADED_IMAGE)
            // Ripristina il testo della TextView
            foodName.text = savedInstanceState.getString(FOOD_NAME_KEY)
        }

        // Registra un ActivityResultLauncher per richiedere il permesso della fotocamera
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                //Se il permesso è concesso, avvia la fotocamera
                launchCamera()
            } else {
                //Se il permesso è negato, registra un errore
                Log.e("PermissionDenied","Camera permission denied")
            }
        }
        // Registra un ActivityResultLauncher per scattare una foto
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    imageView.setImageURI(imageUri)
                    loadedImage=true
                }else{
                    Log.e("CameraError","Image not saved")
                }
            }

        // Registra un ActivityResultLauncher per selezionare un'immagine dalla galleria
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imageUri = result.data?.data!!
                    imageView.setImageURI(imageUri)
                    loadedImage=true
                }
            }

        // Listener per la gestione dello scatto di una foto
        takePictureButton.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }
        // Listener per la gestione della selezione di un'immagine dalla galleria
        pickImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        // Listener per l'avvio della predizione del nome del food
        predictButton.setOnClickListener{
            if(loadedImage) {
                foodName.text = imageClassification(imageView.drawable, model)
            }else{
                foodName.text=getString(R.string.ImageControl)
            }
        }

        //Listener per l'avvio del processing della ricetta
        buttonToRecipeResult.setOnClickListener{ view->
            val intent= Intent(view.context,RecipeLoadingActivity::class.java)
            //Controllo se l'immagine è stata caricata
            if(!loadedImage){
                foodName.text=getString(R.string.ImageControl)
            }
            else{
                //Controllo se il nome del food è stato trovato
                if(foodName.text!=getString(R.string.ImageControl) && foodName.text!=getString(R.string.SelectImage) && foodName.text!=getString(R.string.PressPredictFirst)){
                    //Passaggio alla schermata di processing della ricetta
                    try{
                        intent.putExtra(FOOD_NAME_KEY,foodName.text)
                        intent.putExtra(FOOD_IMAGE_URI_STRING_KEY,imageUri.toString())
                        intent.putExtra(IS_RANDOM_RECIPE_KEY,false)
                        startActivity(intent)}
                    catch (e: UninitializedPropertyAccessException){
                        foodName.text=getString(R.string.ImageControl)
                    }
                }else{
                    //Se il nome del food non è stato trovato, viene mostrato un messaggio di errore
                    foodName.text=getString(R.string.PressPredictFirst)
                }
            }
        }

        //Ritorno alla schermata precedente
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
    //Metodo per avviare la fotocamera
    private fun  launchCamera(){
        val photoFile = File.createTempFile("photo",".jpg",cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        imageUri=FileProvider.getUriForFile(this,"${packageName}.provider",photoFile)
        takePictureLauncher.launch(imageUri)
    }
    //Metodo per controllare il permesso della fotocamera
    private fun checkCameraPermissionAndLaunch() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            permissionLauncher.launch(permission)
        }
    }
    //Metodo per classificare l'immagine
    private fun imageClassification(imageDrawable: Drawable, model: AutoModel1): String{
        //L'immagine viene convertita in un oggetto Bitmap, ridimensionata e
        // infine trasformata in un oggetto TensorImage, adatto alla classificazione di immagini.
        val image = Bitmap.createBitmap((imageDrawable as BitmapDrawable).bitmap)
        imageBitmap = Bitmap.createScaledBitmap(image, 192, 192, true)
        val input = TensorImage.fromBitmap(imageBitmap)

        //Lancio dell'inferenza sul modello
        val outputs = model.process(input)

        //Ottenimento della probabilità di appartenenza ad ogni classe
        // e restituzione della classe con la probabilità più alta
        val probability = outputs.probabilityAsCategoryList
        val best = (probability.maxByOrNull { it.score })!!.label
        return best
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Salva l'URI dell'immagine se presente
        if (::imageUri.isInitialized) {
            outState.putString(FOOD_IMAGE_URI_STRING_KEY, imageUri.toString())
        }
        //Salva il boolean se l'immagine è già stata caricata oppure no
        outState.putBoolean(KEY_LOADED_IMAGE,loadedImage)
        // Salva il testo della TextView
        val foodName = findViewById<TextView>(R.id.foodName)
        outState.putString(FOOD_NAME_KEY, foodName.text.toString())
    }


}