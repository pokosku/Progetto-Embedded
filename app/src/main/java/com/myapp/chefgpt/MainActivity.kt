package com.myapp.chefgpt

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.content.res.Configuration
import java.util.Locale


class MainActivity : AppCompatActivity() {

    //Costanti varie (Intent, preferenze, ...)
    companion object {
        private const val THEME_KEY = "selected_theme"
        private const val LANGUAGE_KEY = "selected_language"
        private const val FOOD_IMAGE_URI_STRING_KEY = "foodimage"
        private const val FOOD_NAME_KEY = "foodname"
        private const val IS_RANDOM_RECIPE_KEY = "is_random_recipe"
        private const val SETTINGS_DIALOG_TAG = "settings_dialog"
        private const val APP_PREFERENCES_KEY = "app_preferences"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //le preferenze dell'app
        //val preferences = getPreferences(MODE_PRIVATE)

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
        setContentView(R.layout.activity_main)
        
        //Settaggio dei bottoni
        val toImagePredictionButton = findViewById<Button>(R.id.toImagePredictionButton)
        val toFavoritesButton = findViewById<Button>(R.id.openFavorites)
        val toRandomRecipeButton = findViewById<Button>(R.id.randomRecipe)
        val settingsButton = findViewById<ImageButton>(R.id.settings)
        
        //Passaggio alla schermata di predizione immagini
        toImagePredictionButton.setOnClickListener{ view ->
            val intent = Intent(view.context, ImagePredictionActivity::class.java)
            startActivity(intent)
        }

        //Passaggio alla schermata dei preferiti
        toFavoritesButton.setOnClickListener{view->
            val intent = Intent(view.context, FavoriteRecipesActivity::class.java)
            startActivity(intent)
        }

        //Passaggio al processing di una ricetta casuale
        toRandomRecipeButton.setOnClickListener{view ->
            val intent = Intent(view.context, RecipeLoadingActivity::class.java)
            intent.putExtra(IS_RANDOM_RECIPE_KEY, true)
            intent.putExtra(FOOD_NAME_KEY, "")
            intent.putExtra(FOOD_IMAGE_URI_STRING_KEY,"")
            startActivity(intent)
        }

        //Apertura fragment di impostazioni
        settingsButton.setOnClickListener{
            settingsButton.isEnabled = false
            val dialog = SettingsDialogFragment()
            dialog.onDismissListener = {
                settingsButton.isEnabled = true
            }
            dialog.show(supportFragmentManager, SETTINGS_DIALOG_TAG)
        }
    }
}