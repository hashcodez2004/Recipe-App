package com.hashdroid.recipe_app.network

data class RecipeResponse(
    val recipes: List<Recipe>
)

data class RecipeResponse2(
    val results: List<Recipe>
)

data class Recipe(
    val title: String,
    val image: String
)
