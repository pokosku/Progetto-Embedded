package com.myapp.chefgpt

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.myapp.chefgpt.utils.Recipe


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toImagePredictionButton = findViewById<Button>(R.id.toImagePredictionButton)
        val toFavorites = findViewById<Button>(R.id.openFavorites)
        val toHistory = findViewById<Button>(R.id.openHistory)


        toImagePredictionButton.setOnClickListener{ view ->
            val intent = Intent(view.context, ImagePredictionActivity::class.java)
            startActivity(intent)
        }
        //TODO: icone per le altre schermate, barra e impostazioni

        toFavorites.setOnClickListener{view->
            val intent = Intent(view.context, FavoriteRecipesActivity::class.java)
            startActivity(intent)
        }
    }
}