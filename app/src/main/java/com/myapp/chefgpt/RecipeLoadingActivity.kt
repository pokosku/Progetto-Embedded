package com.myapp.chefgpt

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.bumptech.glide.Glide

class RecipeLoadingActivity : AppCompatActivity(){
    class LoadingActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_recipeloading)

            val imageView = findViewById<ImageView>(R.id.cookinGif)
            Glide.with(this)
                .asGif()
                .load(R.drawable.cookin) // metti il tuo file gif in res/drawable
                .into(imageView)

            // Avvia l’inferenza in background
            lifecycleScope.launch {
                val result = withContext(Dispatchers.Default) {
                    runInference() // metodo che richiama il tuo modello
                }

                // Passa i risultati all'activity successiva
                val intent = Intent(view.context, ResultActivity::class.java)
                intent.putExtra("inference_result", result)
                startActivity(intent)
                finish() // chiude la schermata di caricamento
            }
        }

        private fun runInference(): String {
            // TODO: integra la tua logica di inferenza
            return "Risultato esempio"
        }
    }

}