package com.myapp.chefgpt.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//Data Access Object di una ricetta per l'interazione con il database Room
//I metodi Query utilizzano le Kotlin coroutines per eseguire le operazioni in background (suspend)
//Questo fa si che i task di lettura siano efficienti e non bloccanti
@Dao
interface RecipeDao {
    //Query per ottenere tutte le ricette
    @Query("SELECT * FROM recipe")
    fun getAll(): LiveData<List<Recipe>>

    //Query per ottenere una ricetta in base al nome
    @Query("SELECT * FROM recipe WHERE name == :name")
    suspend fun findByName(name: String): Recipe?

    //Query per inserire o rimpiazzare una ricetta nel database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe)

    //Query per eliminare una ricetta dal database
    @Delete
    suspend fun delete(recipe: Recipe)
}