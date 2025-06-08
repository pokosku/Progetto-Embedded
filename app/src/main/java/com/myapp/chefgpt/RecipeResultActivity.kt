package com.myapp.chefgpt


import android.content.Context
import android.content.res.Configuration
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
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.myapp.chefgpt.helpers.MarkdownHelper


import com.myapp.chefgpt.database.Recipe
import com.myapp.chefgpt.database.RecipeViewModel
import java.util.Date
import java.util.Locale

//Activity di lettura del risultato dell'inferenza di Natural Language Processing per
//l'ottenimento della ricetta.

class RecipeResultActivity : AppCompatActivity(){

    //ViewModel per la gestione del database
    private lateinit var mRecipeViewModel: RecipeViewModel

    private var buttonToRecipeResultEnabled: Boolean = true

    //Costanti varie (Intent, preferenze, ...)
    companion object {
        private const val BUTTON_STATE = "button_state"
        private const val LANGUAGE_KEY = "selected_language"
        private const val FOOD_IMAGE_URI_STRING_KEY = "food_image"
        private const val FOOD_NAME_KEY = "food_name"
        private const val IS_RANDOM_RECIPE_KEY = "is_random_recipe"
        private const val INFERENCE_RESULT_KEY = "inference_result"
        private const val APP_PREFERENCES_KEY = "app_preferences"
        private const val THEME_KEY = "selected_theme"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
        setContentView(R.layout.activity_reciperesult)
        //ViewModel
        mRecipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        //View di testo e immagine
        val textView = findViewById<TextView>(R.id.textRecipe)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val toFavoriteRecipes = findViewById<Button>(R.id.addToFavourite)
        val toolbarView = findViewById<View>(R.id.toolbar)
        val backButton = toolbarView.findViewById<ImageButton>(R.id.back)

        //Recupero dei dati passati tramite Intent da RecipeLoadingActivity
        val imageUriString = intent.getStringExtra(FOOD_IMAGE_URI_STRING_KEY)
        val recipeResult = intent.getStringExtra(INFERENCE_RESULT_KEY)!!
        val foodName = intent.getStringExtra(FOOD_NAME_KEY)

        //Controllo se è una ricetta casuale
        val isRandomRecipe = intent.getBooleanExtra(IS_RANDOM_RECIPE_KEY, false)

        val imageUri = Uri.parse(imageUriString)



        //Recupero dello stato del pulsante "Aggiungi alla lista dei preferiti"
        if (savedInstanceState != null) {
                toFavoriteRecipes.setEnabled(savedInstanceState.getBoolean(BUTTON_STATE))
                buttonToRecipeResultEnabled = savedInstanceState.getBoolean(BUTTON_STATE)
        }

        // Se è una ricetta casuale viene caricata un immagine placeholder
        if(isRandomRecipe){
            imageView.setImageResource(R.drawable.empty)
        }
        else{
            imageView.setImageURI(imageUri)
        }

        //Aggiunta del titolo alla ricetta, formattazione della ricetta tramite MarkdownHelper
        val recipeResultWithTitle = "# $foodName \n $recipeResult"
        MarkdownHelper(recipeResultWithTitle, this, textView).format()

        //Ottenimento della ricetta dal Room database tramite ViewModel
        mRecipeViewModel.findRecipe(foodName!!)

        //Listener per la gestione del pulsante "Aggiungi alla lista dei preferiti"
        toFavoriteRecipes.setOnClickListener {
            val newRecipe = Recipe(foodName, recipeResult, generateCreationDate())
            var overridable = false

            //Per gestire l'ottenimento della ricetta dal DB viene istanziato un Observer, questo
            //perche' nel ViewModel la ricetta e' gestita come LiveData
            mRecipeViewModel.foundRecipe.observe(this,Observer { recipe ->
                if (recipe != null) {
                    overridable = true
                }
            })
            //Se la ricetta esiste gia' viene chiesto all'utente se vuole sovrascriverla tramite AlertDialog
            if(overridable) {
                val builder = AlertDialog.Builder(this)
                builder.setPositiveButton(if(languageCode=="en") "Yes" else "Si") { _, _ ->
                    insertToDatabase(newRecipe)
                    buttonToRecipeResultEnabled=false
                }
                builder.setNegativeButton("No") { _, _ ->
                    toFavoriteRecipes.setEnabled(true)
                    buttonToRecipeResultEnabled=true
                }
                builder.setTitle(newRecipe.name)
                //Messaggio in base alla lingua settata
                if(languageCode=="en") {
                    builder.setMessage("A recipe for ${newRecipe.name} already exists in your favorites.\nDo you want to overwrite it?")
                }
                if(languageCode=="it") {
                    builder.setMessage("Una ricetta per ${newRecipe.name} esiste già nei tuoi preferiti.Vuoi sovrascriverla?")
                }
                builder.create().show()
            } else {
                //Se la ricetta non esiste viene aggiunta alla lista dei preferiti, viene visualizzato
                //un messaggio di Toast e il pulsante viene disabilitato
                insertToDatabase(newRecipe)
                if(languageCode=="en") {
                    Toast.makeText(this, "Recipe added to favorites", Toast.LENGTH_SHORT).show()
                }
                if(languageCode=="it") {
                    Toast.makeText(this, "Ricetta aggiunta ai preferiti", Toast.LENGTH_SHORT).show()
                }
            }
            toFavoriteRecipes.setEnabled(false)
            buttonToRecipeResultEnabled=false
        }

        //Listener per il pulsante di indietro
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    //Metodo per generare la data di creazione della ricetta formattata
    private fun generateCreationDate() : String{
        val now = Date()
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(now)

    }
    //Metodo per aggiungere la ricetta nel DB tramite ViewModel
    private fun insertToDatabase(recipe: Recipe) {
        mRecipeViewModel.addRecipe(recipe)
    }
    //Salvataggio dello stato
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BUTTON_STATE, buttonToRecipeResultEnabled)
    }
}