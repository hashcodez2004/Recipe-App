package com.hashdroid.recipe_app

import com.hashdroid.recipe_app.network.RecipeResponse
import com.hashdroid.recipe_app.network.RecipeResponse2
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {
    @GET("recipes/random")
    fun getRandomRecipes(
        @Query("number") number: Int,
        @Query("apiKey") apiKey: String
    ): Call<RecipeResponse>

    @GET("recipes/complexSearch")
    fun getAllRecipes(
        @Query("number") number: Int,
        @Query("apiKey") apiKey: String
    ): Call<RecipeResponse2>
}

