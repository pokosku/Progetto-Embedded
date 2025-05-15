package com.myapp.chefgpt.utils

import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView


class RecipeAdapter(private val recipes: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeAdapter.RecipeViewHolder {
        TODO("Not yet implemented")
    }


    override fun onBindViewHolder(holder: RecipeAdapter.RecipeViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}