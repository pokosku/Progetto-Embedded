package com.myapp.chefgpt

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
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
            val newRecipe = Recipe(foodName!!, recipeResult, getCreationDate())

            mRecipeViewModel.findRecipe(foodName)
            mRecipeViewModel.foundRecipe.observe(this,Observer { recipe ->
                if (recipe != null) {
                    val builder = AlertDialog.Builder(this)
                    builder.setPositiveButton("Yes") { _, _ ->
                        insertToDatabase(newRecipe)
                    }
                    builder.setNegativeButton("No") { _, _ -> }
                    builder.setTitle(recipe.name)
                    builder.setMessage("A recipe for ${recipe.name} already exists in your favorites. Do you want to overwrite it?")
                    builder.create().show()
                } else {
                    insertToDatabase(newRecipe)
                    Toast.makeText(this, "Recipe added to favorites", Toast.LENGTH_SHORT).show()
                }
            })

        }


        val toolbarView = findViewById<View>(R.id.toolbar)

        val backButton = toolbarView.findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val settingsButton = toolbarView.findViewById<ImageButton>(R.id.settings)
        settingsButton.setOnClickListener{}
    }

    fun getCreationDate() : String{
        val now = Date()
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(now)

    }
    //TODO: gestire vincolo chiave primaria
    fun insertToDatabase(recipe: Recipe) {
        recipe.description = removePrefixUntilIngredients(recipe.description)
        mRecipeViewModel.addRecipe(recipe)
    }

//    fun findByNameInDatabase(name: String): Recipe? {
//        return mRecipeViewModel.findRecipe(name)
//    }
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