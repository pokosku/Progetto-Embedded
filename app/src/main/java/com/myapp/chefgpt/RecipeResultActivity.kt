package com.myapp.chefgpt

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeResultActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reciperesult)

        val textView: TextView = findViewById(R.id.textRecipe)
        val imageView: ImageView= findViewById(R.id.imageView)

        val imageUriString = intent.getStringExtra("imageURI")
        val recipeResult = intent.getStringExtra("inference_result")
        val imageUri = Uri.parse(imageUriString)
        imageView.setImageURI(imageUri)

        textView.text=recipeResult

    }
}