package com.myapp.chefgpt

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.bumptech.glide.Glide
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
import kotlinx.coroutines.withContext

class RecipeLoadingActivity : AppCompatActivity(){
    private var llmInference: LlmInference? = null
    private lateinit var loadingImageView: ImageView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_recipeloading)

            val imageUriString = intent.getStringExtra("foodimage")
            val foodName = intent.getStringExtra("foodname")!!
            loadingImageView = findViewById(R.id.loadingGif)

            // Mostra la GIF di inferenza
            showPerformingInferenceGif()

            lifecycleScope.launch {

                // Carica il modello in background
                llmInference = withContext(Dispatchers.Default) {
                    loadLlmModel()
                }

                if (llmInference != null) {

                    // Esegui l'inferenza solo se il modello è stato caricato con successo
                    val result = withContext(Dispatchers.Default) {
                        runInference(foodName)
                    }

                    // Passa i risultati all'activity successiva
                    val intent = Intent(this@RecipeLoadingActivity, RecipeResultActivity::class.java)
                    intent.putExtra("imageURI",imageUriString)
                    intent.putExtra("inference_result", result)
                    intent.putExtra("foodname", foodName)
                    startActivity(intent)
                    finish()
                } else {
                    // TODO: Gestisci l'errore di caricamento del modello

                }
            }
        }

    private fun loadLlmModel(): LlmInference? {
        return try {
            val taskOptions = LlmInferenceOptions.builder()
                    .setModelPath("/data/local/tmp/llm/gemma3-1B-it-int4.task")
                    .setMaxTopK(64)
                    .setPreferredBackend(LlmInference.Backend.CPU)
                    .build()
                LlmInference.createFromOptions(this,taskOptions)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
    private fun runInference(foodname : String): String {
        return llmInference?.generateResponse("Give me the recipe for $foodname" +
                    " in not so many words") ?: "Error loading model"
    }

    private fun showPerformingInferenceGif() {
        Glide.with(this)
            .asGif()
            .load(R.drawable.performing_inference) // Sostituisci con il nome del tuo file gif
            .into(loadingImageView)
    }
    override fun onDestroy() {
        super.onDestroy()
        llmInference?.close()
        llmInference = null
    }
}