package com.hashdroid.recipe_app.network

data class RecipeResponse(
    val recipes: List<Recipe>
)

data class Recipe(
    val title: String,
    val image: String
)
