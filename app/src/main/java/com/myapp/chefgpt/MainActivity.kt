package com.myapp.chefgpt

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private var areSettingsOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toImagePredictionButton = findViewById<Button>(R.id.toImagePredictionButton)
        val toFavorites = findViewById<Button>(R.id.openFavorites)
        val toHistory = findViewById<Button>(R.id.openHistory)
        val settingsButton = findViewById<ImageButton>(R.id.settings)


        toImagePredictionButton.setOnClickListener{ view ->
            val intent = Intent(view.context, ImagePredictionActivity::class.java)
            startActivity(intent)
        }

        toFavorites.setOnClickListener{view->
            val intent = Intent(view.context, FavoriteRecipesActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener{
            if(!areSettingsOpened){
                areSettingsOpened = true
                val dialog = SettingsDialogFragment()
                dialog.onDismissListener = {
                    areSettingsOpened = false
                }
                dialog.show(supportFragmentManager, "settings_dialog")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        areSettingsOpened = false
    }

}