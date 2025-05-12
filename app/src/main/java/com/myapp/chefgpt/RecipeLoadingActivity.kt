package com.myapp.chefgpt

import android.content.Intent
import android.net.Uri
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

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_recipeloading)

            val imageUriString = intent.getStringExtra("foodimage")
            val foodName = intent.getStringExtra("foodname")!!

            val imageView = findViewById<ImageView>(R.id.cookinGif)
            Glide.with(this)
                .asGif()
                .load(R.drawable.cookin) // metti il tuo file gif in res/drawable
                .into(imageView)

            // Avvia l’inferenza in background
            lifecycleScope.launch {
                // Passa i risultati all'activity successiva
                val result = withContext(Dispatchers.Default) {
                    runInference(foodName) // metodo che richiama il tuo modello
                }
                val intent = Intent(this@RecipeLoadingActivity, RecipeResultActivity::class.java)

                intent.putExtra("imageURI",imageUriString)
                intent.putExtra("inference_result", result)
                //passo anche il foodname per propagarlo alla favoriteRecipesActivity
                intent.putExtra("foodname", foodName)
                startActivity(intent)
                finish() // chiude la schermata di caricamento
            }
        }

        private fun runInference(foodname : String): String {
            // Inizializzazione delle opzioni per l'inferenza LLM
            val taskOptions = LlmInferenceOptions.builder()
                .setModelPath("/data/local/tmp/llm/gemma3-1B-it-int4.task")
                .setMaxTopK(64)
                .setPreferredBackend(LlmInference.Backend.CPU)
                .build()

            // Creazione di un istanza di LlmInference utilizzando le opzioni specificate
            val llmInference = LlmInference.createFromOptions(this, taskOptions)

            //TODO: scegliere un prompt giusto per i separatori (parsing di ingredienti e step)
            val output : String = llmInference.generateResponse("Give me the recipe for $foodname, do not add any other words")
            return output
        }
    }