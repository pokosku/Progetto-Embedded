package com.myapp.chefgpt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapp.chefgpt.utils.Recipe
import com.myapp.chefgpt.utils.RecipeAdapter
import com.myapp.chefgpt.utils.RecipeViewModel


class FavoriteRecipesActivity : AppCompatActivity() {
    private lateinit var mRecipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_recipes)


        val recipeAdapter = RecipeAdapter()
        val recyclerView : RecyclerView = findViewById(R.id.recipe_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recipeAdapter

        mRecipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        mRecipeViewModel.getAll.observe(this, Observer { recipe ->
            recipeAdapter.setData(recipe)
        })

        //TODO: sistemare grafica recyclerview

    }

}