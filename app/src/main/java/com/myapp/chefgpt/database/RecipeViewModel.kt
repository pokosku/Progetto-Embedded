package com.myapp.chefgpt.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//Classe ViewModel per la gestione del ciclo di vita del database e dei dati nelle varie activity

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    //LiveData per ottenere tutte le ricette presenti nel database dalla Repository
    val getAll: LiveData<List<Recipe>>

    private val repository: RecipeRepository
    //LiveData osservabile per il risultato della ricerca di una ricetta
    private val _foundRecipe = MutableLiveData<Recipe?>()
    val foundRecipe: LiveData<Recipe?> = _foundRecipe

    //Inizializzazione del repository e della LiveData getAll
    init {
        val recipeDao = RecipeDatabase.getDatabase(application).recipeDao()
        repository = RecipeRepository(recipeDao)
        getAll = repository.getAll
    }

    //Metodi per aggiungere, eliminare e cercare ricette nella Repository
    //Eseguiti in un thread separato utilizzando viewModelScope.launch dei metodi di Repository (suspend)
    fun addRecipe(recipe: Recipe) {
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