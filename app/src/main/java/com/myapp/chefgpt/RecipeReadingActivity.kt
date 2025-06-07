package com.myapp.chefgpt

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.myapp.chefgpt.helpers.MarkdownHelper

class RecipeReadingActivity : AppCompatActivity() {

    companion object{
        private const val RECIPE_NAME_KEY = "recipe_name"
        private const val RECIPE_DESCRIPTION_KEY = "recipe_desc"
        private const val SETTINGS_DIALOG_TAG = "settings_dialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_reading)

        val titleTextView= findViewById<TextView>(R.id.recipe_name_reading)
        val descriptionTextView = findViewById<TextView>(R.id.recipe_description_reading)

        val toolbarView = findViewById<View>(R.id.toolbar)
        val backButton = toolbarView.findViewById<ImageButton>(R.id.back)
        val settingsButton = toolbarView.findViewById<ImageButton>(R.id.settings)

        titleTextView.text = intent.getStringExtra(RECIPE_NAME_KEY)
        val recipeResult = intent.getStringExtra(RECIPE_DESCRIPTION_KEY)
        MarkdownHelper(recipeResult!!, this, descriptionTextView).format()

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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