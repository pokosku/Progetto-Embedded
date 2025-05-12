package com.myapp.chefgpt.utils

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Recipe::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null
        fun getDatabase(context: Context): MyDatabase {
            // Se l'istanza è null, crea il database in un blocco sincronizzato
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Usa il contesto dell'applicazione per evitare memory leak
                    MyDatabase::class.java, // La classe del tuo database
                    "recipes_database" // Il nome del file del database
                )
                    // Aggiungi qui eventuali Type Converters se li hai definiti
                    // .addTypeConverter(YourTypeConverter()) // Se il converter richiede un'istanza
                    // .addTypeConverter(YourTypeConverter::class) // Se il converter è un oggetto o non richiede un'istanza

                    // Aggiungi strategie di fallback per le migrazioni se necessario
                    // .fallbackToDestructiveMigration() // Distrugge e ricrea il DB in caso di migrazione mancante

                    .build() // Costruisce l'istanza del database
                INSTANCE = instance // Assegna l'istanza creata alla variabile INSTANCE
                instance // Restituisce l'istanza
            }
        }
    }
}