package com.myapp.chefgpt

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.myapp.chefgpt.database.Recipe
import com.myapp.chefgpt.database.RecipeAdapter
import com.myapp.chefgpt.database.RecipeViewModel

//Activity contenente la lista delle ricette preferite, viene utilizzata una RecyclerView per
//la gestione dell'elenco.

class FavoriteRecipesActivity : AppCompatActivity() {
    //ViewModel per la gestione del database
    private lateinit var mRecipeViewModel: RecipeViewModel
    companion object {
        private const val APP_PREFERENCES_KEY = "app_preferences"
        private const val LANGUAGE_KEY = "selected_language"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_recipes)
        val preferences = getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val languageCode = preferences.getString(LANGUAGE_KEY, "en") ?: "en"

        //Toolbar per tornare indietro
        val toolbarView = findViewById<View>(R.id.toolbar)
        val backButton = toolbarView.findViewById<ImageButton>(R.id.back)

        //RecyclerView per la visualizzazione della lista delle ricette preferite
        val recyclerView : RecyclerView = findViewById(R.id.recipe_recycler_view)

        //ViewModel
        mRecipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        //Lista fittizia di ricette per inizializzare correttamente l'Adapter
        val recipeAdapter = RecipeAdapter(emptyList<Recipe>(),mRecipeViewModel,languageCode)

        //Observer per aggiornare la lista delle ricette quando cambiano i LiveData contenenti le ricette
        mRecipeViewModel.getAll.observe(this, Observer { recipe ->
            recipeAdapter.setData(recipe)
        })

        //Impostazione dell'Adapter e LayoutManager per la RecyclerView
        recyclerView.adapter = recipeAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Listener per il pulsante di indietro
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

}