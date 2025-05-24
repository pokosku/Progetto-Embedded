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

    companion object {
        private const val THEME_KEY = "selected_theme"
        private const val LANGUAGE_KEY = "selected_language"
        private const val FOOD_IMAGE_KEY = "foodimage"
        private const val FOOD_NAME_KEY = "foodname"
        private const val IS_RANDOM_RECIPE_KEY = "is_random_recipe"
        private const val SETTINGS_DIALOG_TAG = "settings_dialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //le preferenze dell'app
        //val preferences = getPreferences(MODE_PRIVATE)
        val preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        //tema chiaro o scuro
        val theme = preferences.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(theme)

        //lingua
        val languageCode = preferences.getString(LANGUAGE_KEY, "en") ?: "en"
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        this.resources.updateConfiguration(config, this.resources.displayMetrics)

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

        toRandomRecipe.setOnClickListener{view ->
            val intent = Intent(view.context, RecipeLoadingActivity::class.java)
            intent.putExtra(IS_RANDOM_RECIPE_KEY, true)
            intent.putExtra(FOOD_NAME_KEY, "")
            intent.putExtra(FOOD_IMAGE_KEY,"")
            startActivity(intent)
        }
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