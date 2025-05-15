package com.myapp.chefgpt

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
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


        val toolbarView = findViewById<View>(R.id.toolbar)

        val backButton = toolbarView.findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val settingsButton = toolbarView.findViewById<ImageButton>(R.id.settings)
        settingsButton.setOnClickListener{}
    }

    //TODO: Implementare la RecyclerView o altro per gestire i preferiti
}