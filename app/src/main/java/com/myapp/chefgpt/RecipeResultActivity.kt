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
import com.myapp.chefgpt.helpers.MarkdownHelper


import com.myapp.chefgpt.database.Recipe
import com.myapp.chefgpt.database.RecipeViewModel
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

        val recipeResultWithTitle = "# $foodName \n $recipeResult"


        MarkdownHelper(recipeResultWithTitle, this, textView).format()


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
                builder.setMessage("A recipe for ${newRecipe.name} already exists in your favorites.\nDo you want to overwrite it?")
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


    private fun getCreationDate() : String{
        val now = Date()
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(now)

    }
    private fun insertToDatabase(recipe: Recipe) {
        mRecipeViewModel.addRecipe(recipe)
    }

}