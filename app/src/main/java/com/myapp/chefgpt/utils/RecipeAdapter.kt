package com.myapp.chefgpt.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.myapp.chefgpt.R


class RecipeAdapter(private var recipeList : List<Recipe> =
    emptyList<Recipe>()) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {


    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipe_name)
        private val creationDateTextView: TextView = itemView.findViewById(R.id.creation_date)

        fun bind(recipe: Recipe){
            recipeNameTextView.text = recipe.name
            creationDateTextView.text = recipe.creationDate
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_row, parent, false)

        //val textView = view.findViewById<TextView>(R.id.recipe_name)
        //textView.setOnClickListener ... qua si puo mettere l'azione da eseguire quando si clicca sull'elemento
        val deleteButton = view.findViewById<Button>(R.id.delete_recipe_btn)
        //TODO: aggiungere funzione per eliminare una ricetta dai preferiti
        deleteButton.setOnClickListener {
            //viewModel.deleteRecipe(recipes[adapterPosition])
        }

        return RecipeViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipeList[position])
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun setData(recipe: List<Recipe>) {
        this.recipeList = recipe
        notifyDataSetChanged()
    }

}