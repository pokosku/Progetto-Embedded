package com.myapp.chefgpt.utils


//questa classe servira nell'utilizzo del Database per salvare le ricette
//come idea si puo utilizzare Room per gestire il database

data class Recipe(
    val name: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val creationDate: String
) {
    //TODO: metodo per parsare gli ingredienti
    fun parseIngredients(ingredientsString: String) {

    }

    //TODO: metodo per parsare gli steps
    fun parseSteps(stepsString: String) {

    }
}
