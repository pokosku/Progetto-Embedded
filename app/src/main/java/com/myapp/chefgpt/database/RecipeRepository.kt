package com.myapp.chefgpt.database

import androidx.lifecycle.LiveData

class RecipeRepository(private val recipeDao: RecipeDao) {

    val getAll : LiveData<List<Recipe>> = recipeDao.getAll()

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