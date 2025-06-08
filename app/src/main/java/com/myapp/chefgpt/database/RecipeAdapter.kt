package com.myapp.chefgpt.database

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.recyclerview.widget.RecyclerView
import com.myapp.chefgpt.R
import com.myapp.chefgpt.RecipeReadingActivity


//Adapter per la RecyclerView che mostra le ricette preferite

class RecipeAdapter(private var recipeList : List<Recipe> =
    emptyList<Recipe>(),
    private val mRecipeViewModel: RecipeViewModel, private val language: String) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    //ViewHolder per ogni elemento della lista
    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Nome ricetta, data creazione e pulsante di delete
        private val recipeNameTextView: TextView = itemView.findViewById(R.id.recipe_name)
        private val creationDateTextView: TextView = itemView.findViewById(R.id.creation_date)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_recipe_btn)
        lateinit var language : String

        //Costanti di key names
        companion object{
            private const val RECIPE_NAME_KEY = "recipe_name"
            private const val RECIPE_DESCRIPTION_KEY = "recipe_desc"
        }

        //Metodo per associare i dati di una ricetta alla ViewHolder
        fun bind(recipe: Recipe, mRecipeViewModel: RecipeViewModel){
            recipeNameTextView.text = recipe.name
            creationDateTextView.text = recipe.creationDate

            //Listener per il pulsante di delete, chiede conferma all'utente
            //Gestito con le opzioni in base alla lingua
            deleteButton.setOnClickListener {
                val builder = AlertDialog.Builder(itemView.context)
                builder.setPositiveButton(if(language=="en") "Yes" else "Si") {_, _ ->
                    mRecipeViewModel.deleteRecipe(recipe)
                    if(language=="en")
                        Toast.makeText(itemView.context, "Recipe deleted", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(itemView.context, "Ricetta eliminata", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("No"){ _,_ -> }
                builder.setTitle(recipe.name)
                builder.setMessage(if(language=="en") "Do you want to delete this recipe?" else "Vuoi eliminare questa ricetta?")
                builder.create().show()
            }
            //Se si clicca sul nome della ricetta la si puo' leggere
            recipeNameTextView.setOnClickListener {
                readRecipe(recipe)
            }
        }
        //Metodo per aprire la schermata di lettura della ricetta
        private fun readRecipe(recipe: Recipe){
            val intent = Intent(itemView.context, RecipeReadingActivity::class.java)
            intent.putExtra(RECIPE_NAME_KEY, recipe.name)
            intent.putExtra(RECIPE_DESCRIPTION_KEY, recipe.description)
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
        holder.language = language
        holder.bind(recipeList[position], mRecipeViewModel)
    }
//Metodo per ottenere la dimensione della lista
    override fun getItemCount(): Int {
        return recipeList.size
    }
//Metodo per aggiornare la lista delle ricette
    fun setData(recipe: List<Recipe>) {
        this.recipeList = recipe
        notifyDataSetChanged()
    }
}