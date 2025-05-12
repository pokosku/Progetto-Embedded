package com.myapp.chefgpt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.myapp.chefgpt.utils.Recipe


class RecipeResultActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reciperesult)

        val textView: TextView = findViewById<TextView>(R.id.textRecipe)
        val imageView: ImageView= findViewById<ImageView>(R.id.imageView)
        val toFavoriteRecipesBtn : Button = findViewById<Button>(R.id.addToFavourite)

        val imageUriString = intent.getStringExtra("imageURI")
        val recipeResult = intent.getStringExtra("inference_result")
        val foodName = intent.getStringExtra("foodname")


        val imageUri = Uri.parse(imageUriString)
        imageView.setImageURI(imageUri)

        textView.text=recipeResult

        toFavoriteRecipesBtn.setOnClickListener {view ->
            val intent = Intent(view.context, FavoriteRecipesActivity::class.java)
            intent.putExtra("recipe", recipeResult)
            intent.putExtra("foodname", foodName)
            startActivity(intent)
        }
    }
}