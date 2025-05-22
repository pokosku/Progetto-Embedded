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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //le preferenze dell'app
        //val preferences = getPreferences(MODE_PRIVATE)
        val preferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        //tema chiaro o scuro
        val theme = preferences.getInt(THEME_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(theme)

        //lingua
        val langCode = preferences.getString(LANGUAGE_KEY, "en") ?: "en"
        val locale = Locale(langCode)
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