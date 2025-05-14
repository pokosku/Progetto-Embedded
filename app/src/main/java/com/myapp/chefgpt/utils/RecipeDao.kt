package com.myapp.chefgpt.utils

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe")
    fun getAll(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE name LIKE :name LIMIT 1")
    suspend fun findByName(name: String): Recipe

    @Insert
    suspend fun insert(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)
}