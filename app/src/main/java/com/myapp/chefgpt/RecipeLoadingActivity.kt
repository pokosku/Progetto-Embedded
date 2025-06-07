package com.myapp.chefgpt

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.bumptech.glide.Glide
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
import com.google.mediapipe.tasks.genai.llminference.ProgressListener
import com.myapp.chefgpt.ml.AutoModel1
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage
import kotlin.random.Random

class RecipeLoadingActivity : AppCompatActivity(){
    //Variabile di tipo LlmInference per l'istanza del modello di Natural Language Processing
    private var llmInference: LlmInference? = null

    //Variabile di tipo ImageView per l'immagine di caricamento (GIF)
    private lateinit var loadingImageView: ImageView

    //Costanti varie (Intent, preferenze, ...)
    companion object{
        private const val LANGUAGE_KEY = "selected_language"
        private const val FOOD_IMAGE_URI_STRING_KEY = "food_image"
        private const val FOOD_NAME_KEY = "food_name"
        private const val IS_RANDOM_RECIPE_KEY = "is_random_recipe"
        private const val INFERENCE_RESULT_KEY = "inference_result"
        private const val APP_PREFERENCES_KEY = "app_preferences"
    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_recipeloading)

            //Recupero dei dati passati tramite Intent da ImagePredictionActivity
            val imageUriString = intent.getStringExtra(FOOD_IMAGE_URI_STRING_KEY)
            var foodName = intent.getStringExtra(FOOD_NAME_KEY)!!
            val isRandomRecipe = intent.getBooleanExtra(IS_RANDOM_RECIPE_KEY, false)

            loadingImageView = findViewById(R.id.loadingGif)

            //Recupero della preferenza del linguaggio
            val preferences = getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE)
            val languageCode = preferences.getString(LANGUAGE_KEY, "en") ?: "en"

            //Se è una ricetta casuale, viene generato un nome casuale
            if(isRandomRecipe){
                foodName = generateRandomFoodname()
            }
            //Avvio della gif di caricamento
            showPerformingInferenceGif()

            //Vengono utilizzate le coroutine per gestire le operazioni di inferenza in thread separati
            // per evitare che il thread principale sia bloccato

            lifecycleScope.launch {
                //Caricamento del modello di NLP in un thread separato
                llmInference = withContext(Dispatchers.IO){
                    loadLlmModel()
                }

                //Se il modello è stato caricato correttamente, viene avviata l'inferenza
                if (llmInference != null) {
                    startInference(foodName, imageUriString, isRandomRecipe,languageCode)
                //Messaggio di errore in caso di fallimento
                } else {
                    AlertDialog.Builder(this@RecipeLoadingActivity)
                        .setTitle("Error")
                        .setMessage("Failed to load the model.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }

    //Metodo principale dell'activity che avvia l'inferenza di Natural Language Processing
    private fun startInference(foodName: String, imageUriString: String?, isRandomRecipe : Boolean, langCode : String){
        //Verra' utilizzata la generazione asincrona della risposta in modo da aver
        //traccia il progresso dell'inferenza.

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        //Impostazione dei parametri per la generazione della risposta
        val maxTokens = 350
        var currentTokens = 0
        var prompt=""
        if(langCode=="en") {
             prompt="Write only the ingredients and instructions to make [$foodName], for one person.\n" +
                    "No introduction, no title, no conclusion, no notes. Keep it short and clear."
        }
        if(langCode=="it"){
            prompt="Scrivi solo gli ingredienti e le istruzioni per fare [$foodName], per una persona.\n" +
                    "No introduzioni, no titoli, no conclusioni, no note. Tienila chiara e corta."
        }

        //Generazione della risposta
        val resultBuilder = StringBuilder()
        llmInference?.generateResponseAsync(
            prompt,
            ProgressListener<String>{ partialResult, done ->

                currentTokens++
                val progressPercent = (currentTokens * 100) / maxTokens

                //Per visualizzare il progresso live della generazione della risposta occorre
                //runnare la stessa nel thread principale (UI)
                runOnUiThread {
                    resultBuilder.append(partialResult)

                    progressBar.progress = progressPercent

                    //Quando la generazione della risposta è terminata, viene passato il risultato
                    //tramite Intent a RecipeResultActivity
                    if (done) {
                        progressBar.progress = 100
                        val intent = Intent(this@RecipeLoadingActivity, RecipeResultActivity::class.java)
                        if(imageUriString != null)
                            intent.putExtra(FOOD_IMAGE_URI_STRING_KEY,imageUriString)
                        intent.putExtra(INFERENCE_RESULT_KEY, resultBuilder.toString())
                        intent.putExtra(FOOD_NAME_KEY, foodName)
                        intent.putExtra(IS_RANDOM_RECIPE_KEY, isRandomRecipe)
                        startActivity(intent)
                        finish()
                    }
                }
            })

    }

    //Metodo per caricare il modello di Natural Language Processing
    private fun loadLlmModel(): LlmInference? {
        return try {
            val taskOptions = LlmInferenceOptions.builder()
                    .setModelPath("/data/local/tmp/llm/gemma3-1B-it-int4.task")
                    .setMaxTopK(64)
                    .setPreferredBackend(LlmInference.Backend.CPU)
                    .setMaxTokens(350)
                    .build()
                LlmInference.createFromOptions(this,taskOptions)
            }
        catch (e: Exception){
                e.printStackTrace()
                null
            }
    }

    //Metodo per generare un nome casuale di food
    private fun generateRandomFoodname() : String{
        val model = AutoModel1.newInstance(this)
        val shallowInput = TensorImage.fromBitmap(Bitmap.createBitmap(192,192,Bitmap.Config.ARGB_8888))
        val outputs = model.process(shallowInput)
        return outputs.probabilityAsCategoryList[Random.nextInt(2023)].label
    }

    //Metodo per mostrare la gif di caricamento
    private fun showPerformingInferenceGif() {
        Glide.with(this)
            .asGif()
            .load(R.drawable.performing_inference)
            .into(loadingImageView)
    }
    //Rilascio delle risorse alla distruzione dell'activity
    override fun onDestroy() {
        super.onDestroy()
        llmInference?.close()
        llmInference = null
    }
}