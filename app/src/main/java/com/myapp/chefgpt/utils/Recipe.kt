package com.myapp.chefgpt.utils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


//questa classe servira nell'utilizzo del Database per salvare le ricette
//come idea si puo utilizzare Room per gestire il database

@Entity(tableName = "recipe")
data class Recipe(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "creation_date") val creationDate: String,
)

