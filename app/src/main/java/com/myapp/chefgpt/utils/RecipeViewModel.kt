package com.myapp.chefgpt.utils

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    val getAll: LiveData<List<Recipe>>
    private val repository: RecipeRepository
    private val _foundRecipe = MutableLiveData<Recipe?>()
    val foundRecipe: LiveData<Recipe?> = _foundRecipe

    init {
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
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRecipe(recipe)
        }
    }

    fun findRecipe(name: String){
        viewModelScope.launch {
            _foundRecipe.value = repository.findRecipe(name)
        }
    }
}