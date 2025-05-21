package com.myapp.chefgpt

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toImagePredictionButton = findViewById<Button>(R.id.toImagePredictionButton)
        val toFavorites = findViewById<Button>(R.id.openFavorites)
        val toRandomRecipe = findViewById<Button>(R.id.randomRecipe)
        val settingsButton = findViewById<ImageButton>(R.id.settings)


        toImagePredictionButton.setOnClickListener{ view ->
            val intent = Intent(view.context, ImagePredictionActivity::class.java)
            startActivity(intent)
        }

        toFavorites.setOnClickListener{view->
            val intent = Intent(view.context, FavoriteRecipesActivity::class.java)
            startActivity(intent)
        }

        //TODO: gestire la ricetta random
        toRandomRecipe.setOnClickListener{view ->
            val intent = Intent(view.context, RecipeLoadingActivity::class.java)
            intent.putExtra("is_random_recipe", true)
            intent.putExtra("foodname", "")
            intent.putExtra("foodimage","")
            startActivity(intent)
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
}