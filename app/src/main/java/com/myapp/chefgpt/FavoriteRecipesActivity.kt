package com.myapp.chefgpt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.myapp.chefgpt.utils.RecipeViewModel


class FavoriteRecipesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var mRecipeViewModel: RecipeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_recipes)

        mRecipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

        val recipeList = mRecipeViewModel.getAll.value!!



        recyclerView = findViewById<RecyclerView>(R.id.recipeRecyclerView)
    }

    //TODO: Implementare la RecyclerView o altro per gestire i preferiti
}