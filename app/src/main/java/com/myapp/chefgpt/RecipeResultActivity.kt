package com.myapp.chefgpt


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


import com.myapp.chefgpt.utils.Recipe
import com.myapp.chefgpt.utils.RecipeViewModel
import io.noties.markwon.Markwon
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

        val toolbarView = findViewById<View>(R.id.toolbar)
        val backButton = toolbarView.findViewById<ImageButton>(R.id.back)
        val settingsButton = toolbarView.findViewById<ImageButton>(R.id.settings)

        val imageUriString = intent.getStringExtra("imageURI")
        var recipeResult = intent.getStringExtra("inference_result")!!

        val foodName = intent.getStringExtra("foodname")

        val imageUri = Uri.parse(imageUriString)
        imageView.setImageURI(imageUri)


        val rawMarkdown = fixNumberedMarkdownList(recipeResult)
        val fixedMarkdown = fixNumberedMarkdownList(rawMarkdown)
        val markwon = Markwon.create(this)
        markwon.setMarkdown(textView, fixedMarkdown)
        //textView.text=recipeResult

        mRecipeViewModel.findRecipe(foodName!!)

        //inserimento della ricetta nel database (preferiti)
        toFavoriteRecipesBtn.setOnClickListener {
            val newRecipe = Recipe(foodName!!, recipeResult, getCreationDate())
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
                }
                builder.setNegativeButton("No") { _, _ ->
                    toFavoriteRecipesBtn.setEnabled(true) }
                builder.setTitle(newRecipe.name)
                builder.setMessage("A recipe for ${newRecipe.name} already exists in your favorites. Do you want to overwrite it?")
                builder.create().show()
            } else {
                insertToDatabase(newRecipe)
                Toast.makeText(this, "Recipe added to favorites", Toast.LENGTH_SHORT).show()
            }
            toFavoriteRecipesBtn.setEnabled(false)
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

    //TODO: spostare questo in una classe visto che lo dobbiamo usare anche in RecipeReadingActivity
    fun fixNumberedMarkdownList(markdown: String): String {
        val lines = markdown.lines()
        val result = StringBuilder()
        val numberedItemRegex = Regex("^\\d+\\.\\s+.*")
        var inList = false

        for (i in lines.indices) {
            val line = lines[i]
            if (line.matches(numberedItemRegex)) {
                inList = true
                result.appendLine(line)
            } else if (inList && line.isNotBlank()) {
                // Se siamo dentro una lista numerata e la riga non inizia con "n. ", la indentiamo
                result.appendLine("   $line")
            } else {
                inList = false
                result.appendLine(line)
            }
        }

        return result.toString()
    }

    fun getCreationDate() : String{
        val now = Date()
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(now)

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