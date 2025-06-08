package com.myapp.chefgpt.database

import androidx.lifecycle.LiveData

//Classe Repository per gestire le operazioni sul database, e' un ulteriore step per
// astrarre la logica di accesso al database
class RecipeRepository(private val recipeDao: RecipeDao) {

    //Oggetto LiveData che contiene tutte le ricette presenti nel database
    //E' possibile osservarlo per essere notificato quando cambiano i dati
    val getAll : LiveData<List<Recipe>> = recipeDao.getAll()

    //Metodi chiamanti alle funzioni corrispondenti nella DAO

    suspend fun addRecipe(recipe: Recipe){
        recipeDao.insert(recipe)
    }
    suspend fun deleteRecipe(recipe: Recipe){
        recipeDao.delete(recipe)
    }
    suspend fun findRecipe(name: String): Recipe? {
        return recipeDao.findByName(name)
    }
}