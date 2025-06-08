package com.myapp.chefgpt.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


//Data class per rappresentare una ricetta.
//Utilizzo di Room DB
@Entity(tableName = "recipe")
data class Recipe(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "creation_date") val creationDate: String,
)

