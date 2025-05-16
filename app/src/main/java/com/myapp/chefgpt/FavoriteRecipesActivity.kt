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

        val recyclerView : RecyclerView = findViewById(R.id.recipe_recycler_view)

        mRecipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        val recipeAdapter = RecipeAdapter(emptyList<Recipe>(),mRecipeViewModel)

        mRecipeViewModel.getAll.observe(this, Observer { recipe ->
            recipeAdapter.setData(recipe)
        })

        recyclerView.adapter = recipeAdapter

        recyclerView.layoutManager = LinearLayoutManager(this)




        //TODO: sistemare grafica recyclerview

    }

}