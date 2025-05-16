package com.myapp.chefgpt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeReadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_reading)

        val titleTextView= findViewById<TextView>(R.id.recipe_name_reading)
        val descriptionTextView = findViewById<TextView>(R.id.recipe_description_reading)

        titleTextView.text = intent.getStringExtra("recipe_name")
        descriptionTextView.text = intent.getStringExtra("recipe_desc")
    }
}