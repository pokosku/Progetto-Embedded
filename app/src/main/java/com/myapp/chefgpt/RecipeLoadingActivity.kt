package com.myapp.chefgpt

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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
    private var llmInference: LlmInference? = null
    private lateinit var loadingImageView: ImageView

    companion object{
        private const val LANGUAGE_KEY = "selected_language"
        private const val FOOD_IMAGE_KEY = "foodimage"
        private const val FOOD_NAME_KEY = "foodname"
        private const val IS_RANDOM_RECIPE_KEY = "is_random_recipe"
    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_recipeloading)

            val imageUriString = intent.getStringExtra(FOOD_IMAGE_KEY)
            var foodName = intent.getStringExtra(FOOD_NAME_KEY)!!
            val isRandomRecipe = intent.getBooleanExtra(IS_RANDOM_RECIPE_KEY, false)

            loadingImageView = findViewById(R.id.loadingGif)

            val preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            val languageCode = preferences.getString(LANGUAGE_KEY, "en") ?: "en"


            if(isRandomRecipe){
                foodName = generateRandomFoodname()
            }
            showPerformingInferenceGif()

            lifecycleScope.launch {
                llmInference = withContext(Dispatchers.IO){
                    loadLlmModel()
                }
                if (llmInference != null) {
                    startInference(foodName, imageUriString, isRandomRecipe,languageCode)

                } else {
                    AlertDialog.Builder(this@RecipeLoadingActivity)
                        .setTitle("Error")
                        .setMessage("Failed to load the model.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }

    private fun startInference(foodName: String, imageUriString: String?, isRandomRecipe : Boolean, langCode : String){
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val maxTokens = 350
        //TODO mi sa che son pochi sti token
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

        val resultBuilder = StringBuilder()
        llmInference?.generateResponseAsync(
            prompt,
            ProgressListener<String>{ partialResult, done ->

                currentTokens++
                val progressPercent = (currentTokens * 100) / maxTokens
                runOnUiThread {
                    resultBuilder.append(partialResult)

                    progressBar.progress = progressPercent

                    if (done) {
                        progressBar.progress = 100
                        val intent = Intent(this@RecipeLoadingActivity, RecipeResultActivity::class.java)
                        if(imageUriString != null)
                            intent.putExtra("imageURI",imageUriString)
                        intent.putExtra("inference_result", resultBuilder.toString())
                        intent.putExtra(FOOD_NAME_KEY, foodName)
                        intent.putExtra(IS_RANDOM_RECIPE_KEY, isRandomRecipe)
                        startActivity(intent)
                        finish()
                    }
                }
            })

    }
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

    private fun generateRandomFoodname() : String{
        val model = AutoModel1.newInstance(this)
        val shallowInput = TensorImage.fromBitmap(Bitmap.createBitmap(192,192,Bitmap.Config.ARGB_8888))
        val outputs = model.process(shallowInput)
        return outputs.probabilityAsCategoryList[Random.nextInt(2023)].label
    }

    private fun showPerformingInferenceGif() {
        Glide.with(this)
            .asGif()
            .load(R.drawable.performing_inference)
            .into(loadingImageView)
    }
    override fun onDestroy() {
        super.onDestroy()
        llmInference?.close()
        llmInference = null
    }
}