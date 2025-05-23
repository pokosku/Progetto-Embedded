package com.myapp.chefgpt

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapp.chefgpt.database.Recipe
import com.myapp.chefgpt.database.RecipeAdapter
import com.myapp.chefgpt.database.RecipeViewModel


class FavoriteRecipesActivity : AppCompatActivity() {
    private lateinit var mRecipeViewModel: RecipeViewModel

    companion object{
        private const val SETTINGS_DIALOG_TAG = "settings_dialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_recipes)

        val toolbarView = findViewById<View>(R.id.toolbar)
        val backButton = toolbarView.findViewById<ImageButton>(R.id.back)
        val settingsButton = toolbarView.findViewById<ImageButton>(R.id.settings)

        val recyclerView : RecyclerView = findViewById(R.id.recipe_recycler_view)

        mRecipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)
        val recipeAdapter = RecipeAdapter(emptyList<Recipe>(),mRecipeViewModel)

        mRecipeViewModel.getAll.observe(this, Observer { recipe ->
            recipeAdapter.setData(recipe)
        })

        recyclerView.adapter = recipeAdapter

        recyclerView.layoutManager = LinearLayoutManager(this)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        settingsButton.setOnClickListener{
            settingsButton.isEnabled = false
            val dialog = SettingsDialogFragment()
            dialog.onDismissListener = {
                settingsButton.isEnabled = true
            }
            dialog.show(supportFragmentManager, SETTINGS_DIALOG_TAG)
        }
    }

}