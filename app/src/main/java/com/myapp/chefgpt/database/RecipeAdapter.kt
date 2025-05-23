package com.myapp.chefgpt.database

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.recyclerview.widget.RecyclerView
import com.myapp.chefgpt.R
import com.myapp.chefgpt.RecipeReadingActivity


class RecipeAdapter(private var recipeList : List<Recipe> =
    emptyList<Recipe>(),
    private val mRecipeViewModel: RecipeViewModel) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {


    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipe_name)
        private val creationDateTextView: TextView = itemView.findViewById(R.id.creation_date)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_recipe_btn)

        fun bind(recipe: Recipe, mRecipeViewModel: RecipeViewModel){
            recipeNameTextView.text = recipe.name
            creationDateTextView.text = recipe.creationDate

            deleteButton.setOnClickListener {
                val builder = AlertDialog.Builder(itemView.context)
                builder.setPositiveButton("Yes") {_, _ ->
                    mRecipeViewModel.deleteRecipe(recipe)
                    Toast.makeText(itemView.context, "Recipe deleted", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("No"){ _,_ -> }
                builder.setTitle(recipe.name)
                builder.setMessage("Do you want to delete this recipe?")
                builder.create().show()
            }

            recipeNameTextView.setOnClickListener {
                readRecipe(recipe)
            }

        }
        private fun readRecipe(recipe: Recipe){
            val intent = Intent(itemView.context, RecipeReadingActivity::class.java)
            intent.putExtra("recipe_name", recipe.name)
            intent.putExtra("recipe_desc", recipe.description)
            itemView.context.startActivity(intent)
        }


    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_row, parent, false)

        return RecipeViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipeList[position], mRecipeViewModel)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun setData(recipe: List<Recipe>) {
        this.recipeList = recipe
        notifyDataSetChanged()
    }

}