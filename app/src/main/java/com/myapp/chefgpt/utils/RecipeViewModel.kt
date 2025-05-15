package com.myapp.chefgpt.utils

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    val getAll : LiveData<List<Recipe>>
    private val repository : RecipeRepository
    init{
        val recipeDao = RecipeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(recipeDao)
        getAll = repository.getAll
    }
    fun addRecipe(recipe: Recipe) {
        //Dispatchers.IO fa runnare il codice in un thread background
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecipe(recipe)
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch (Dispatchers.IO){
            repository.deleteRecipe(recipe)
        }
    }

    fun findRecipe(name: String): Recipe {
        var recipe : Recipe? = null
        viewModelScope.launch {
            recipe = repository.findRecipe(name)
        }
        return recipe!!
    }
}