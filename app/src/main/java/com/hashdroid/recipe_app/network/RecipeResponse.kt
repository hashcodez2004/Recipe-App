package com.hashdroid.recipe_app.network

data class RecipeResponse(
    val recipes: List<Recipe>
)

data class RecipeResponse2(
    val results: List<Recipe>
)

data class Recipe(
    val id: Int,
    val title: String,
    val image: String
)

data class SimilarRecipes(
    val id: Int,
    val title: String,
    val sourceUrl: String,
    val readyInMinutes: Int
)
