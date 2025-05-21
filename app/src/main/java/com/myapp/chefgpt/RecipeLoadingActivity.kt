package com.myapp.chefgpt

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

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_recipeloading)

            val imageUriString = intent.getStringExtra("foodimage")
            var foodName = intent.getStringExtra("foodname")!!
            val isRandomRecipe = intent.getBooleanExtra("is_random_recipe", false)

            loadingImageView = findViewById(R.id.loadingGif)


            if(isRandomRecipe){
                foodName = generateRandomFoodname()
            }
            showPerformingInferenceGif()

            lifecycleScope.launch {
                llmInference = withContext(Dispatchers.IO){
                    loadLlmModel()
                }
                if (llmInference != null) {
                    startInference(foodName, imageUriString, isRandomRecipe)

                } else {
                    AlertDialog.Builder(this@RecipeLoadingActivity)
                        .setTitle("Error")
                        .setMessage("Failed to load the model.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }

    private fun startInference(foodName: String, imageUriString: String?, isRandomRecipe : Boolean){
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val maxTokens = 350
        var currentTokens = 0
        val resultBuilder = StringBuilder()
        llmInference?.generateResponseAsync(
            "Write only the ingredients and instructions to make [$foodName].\n" +
                    "No introduction, no title, no conclusion, no notes. Keep it short and clear.",
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
                        intent.putExtra("foodname", foodName)
                        intent.putExtra("is_random_recipe", isRandomRecipe)
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
                    //TODO : verificare maxTokens
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