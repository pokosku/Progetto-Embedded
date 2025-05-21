package com.myapp.chefgpt.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe")
    fun getAll(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE name == :name")
    suspend fun findByName(name: String): Recipe?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe)


    @Delete
    suspend fun delete(recipe: Recipe)
}