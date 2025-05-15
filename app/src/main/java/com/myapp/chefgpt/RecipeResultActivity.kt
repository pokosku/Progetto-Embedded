package com.myapp.chefgpt

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.myapp.chefgpt.utils.RecipeDatabase
import androidx.room.Room

import com.myapp.chefgpt.utils.Recipe
import com.myapp.chefgpt.utils.RecipeViewModel
import java.util.Date


class RecipeResultActivity : AppCompatActivity(){

    private lateinit var mRecipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reciperesult)

        mRecipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        val textView: TextView = findViewById<TextView>(R.id.textRecipe)
        val imageView: ImageView= findViewById<ImageView>(R.id.imageView)
        val toFavoriteRecipesBtn : Button = findViewById<Button>(R.id.addToFavourite)

        val imageUriString = intent.getStringExtra("imageURI")
        var recipeResult = intent.getStringExtra("inference_result")
        recipeResult = removePrefixUntilIngredients(recipeResult!!)

        val foodName = intent.getStringExtra("foodname")


        val imageUri = Uri.parse(imageUriString)
        imageView.setImageURI(imageUri)

        textView.text=recipeResult


        //inserimento della ricetta nel database (preferiti)
        toFavoriteRecipesBtn.setOnClickListener {
            val recipe = Recipe(foodName!!, recipeResult, Date().toString())
            insertToDatabase(recipe)
            Toast.makeText(this, "Recipe added to favorites", Toast.LENGTH_SHORT).show()
        }


        val toolbarView = findViewById<View>(R.id.toolbar)

        val backButton = toolbarView.findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val settingsButton = toolbarView.findViewById<ImageButton>(R.id.settings)
        settingsButton.setOnClickListener{}
    }

    fun insertToDatabase(recipe: Recipe) {
        mRecipeViewModel.addRecipe(recipe)
    }

    fun removePrefixUntilIngredients(recipeString: String): String {
        val lines = recipeString.lines() // Dividi la stringa in righe

        // Trova l'indice della riga che contiene "**ingredients**"
        val ingredientsLineIndex = lines.indexOfFirst { it.trim().equals("**ingredients**", ignoreCase = true) }

        // Se "**ingredients**" non viene trovato, restituisci la stringa originale o un messaggio di errore
        if (ingredientsLineIndex == -1) {
            return recipeString
        }

        // Unisci le righe a partire dalla riga *successiva* a "**ingredients**"
        // Se ingredientsLineIndex è l'ultimo indice, non ci sono righe successive
        if (ingredientsLineIndex + 1 >= lines.size) {
            return "" // Non c'è contenuto dopo "**ingredients**"
        }

        // Prendi la sottolista di righe a partire dall'indice successivo e uniscile
        val contentAfterIngredients = lines.subList(ingredientsLineIndex + 1, lines.size)
        return contentAfterIngredients.joinToString(separator = "\n") // Unisci le righe con newline
    }
}