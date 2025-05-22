package com.myapp.chefgpt


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.myapp.chefgpt.helpers.MarkdownHelper


import com.myapp.chefgpt.database.Recipe
import com.myapp.chefgpt.database.RecipeViewModel
import java.util.Date


class RecipeResultActivity : AppCompatActivity(){

    private lateinit var mRecipeViewModel: RecipeViewModel
    private var buttonToRecipeResultEnabled: Boolean = true

    companion object {
        private const val BUTTON_STATE = "button_state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reciperesult)

        mRecipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        val textView: TextView = findViewById<TextView>(R.id.textRecipe)
        val imageView: ImageView= findViewById<ImageView>(R.id.imageView)
        val toFavoriteRecipesBtn : Button = findViewById<Button>(R.id.addToFavourite)

        val toolbarView = findViewById<View>(R.id.toolbar)
        val backButton = toolbarView.findViewById<ImageButton>(R.id.back)
        val settingsButton = toolbarView.findViewById<ImageButton>(R.id.settings)

        val imageUriString = intent.getStringExtra("imageURI")
        var recipeResult = intent.getStringExtra("inference_result")!!
        val foodName = intent.getStringExtra("foodname")
        val isRandomRecipe = intent.getBooleanExtra("is_random_recipe", false)

        val imageUri = Uri.parse(imageUriString)

        val prefs = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val langCode = prefs.getString("selected_language", "en") ?: "en"

        if (savedInstanceState != null) {
                toFavoriteRecipesBtn.setEnabled(savedInstanceState.getBoolean(BUTTON_STATE))
                buttonToRecipeResultEnabled = savedInstanceState.getBoolean(BUTTON_STATE)
        }

        // se è una ricetta casuale viene caricata un immagine placeholder
        if(isRandomRecipe){
            imageView.setImageResource(R.drawable.empty)
        }
        else{
            imageView.setImageURI(imageUri)
        }

        val recipeResultWithTitle = "# $foodName \n $recipeResult"


        MarkdownHelper(recipeResultWithTitle, this, textView).format()


        mRecipeViewModel.findRecipe(foodName!!)

        //inserimento della ricetta nel database (preferiti)
        toFavoriteRecipesBtn.setOnClickListener {
            val newRecipe = Recipe(foodName, recipeResult, generateCreationDate())
            var overwritable = false

            mRecipeViewModel.foundRecipe.observe(this,Observer { recipe ->
                if (recipe != null) {
                    overwritable = true
                }
            })
            if(overwritable) {
                val builder = AlertDialog.Builder(this)
                builder.setPositiveButton("Yes") { _, _ ->
                    insertToDatabase(newRecipe)
                    buttonToRecipeResultEnabled=false
                }
                builder.setNegativeButton("No") { _, _ ->
                    toFavoriteRecipesBtn.setEnabled(true)
                    buttonToRecipeResultEnabled=true
                }
                builder.setTitle(newRecipe.name)
                if(langCode=="en") {
                    builder.setMessage("A recipe for ${newRecipe.name} already exists in your favorites.\nDo you want to overwrite it?")
                }
                if(langCode=="it") {
                    builder.setMessage("Una ricetta per ${newRecipe.name} esiste già nei tuoi preferiti.Vuoi sovrascriverla?")
                }
                builder.create().show()
            } else {
                insertToDatabase(newRecipe)
                if(langCode=="en") {
                    Toast.makeText(this, "Recipe added to favorites", Toast.LENGTH_SHORT).show()
                }
                if(langCode=="it") {
                    Toast.makeText(this, "Ricetta aggiunta ai preferiti", Toast.LENGTH_SHORT).show()
                }
            }
            toFavoriteRecipesBtn.setEnabled(false)
            buttonToRecipeResultEnabled=false
        }


        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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


    private fun generateCreationDate() : String{
        val now = Date()
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(now)

    }
    private fun insertToDatabase(recipe: Recipe) {
        mRecipeViewModel.addRecipe(recipe)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUTTON_STATE, buttonToRecipeResultEnabled)
    }

}