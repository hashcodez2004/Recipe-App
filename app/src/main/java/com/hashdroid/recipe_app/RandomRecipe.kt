package com.hashdroid.recipe_app

import com.hashdroid.recipe_app.network.RecipeResponse
import com.hashdroid.recipe_app.network.RecipeResponse2
import com.hashdroid.recipe_app.network.RecipieView
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
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

    @GET("recipes/{id}/information")
    fun getRecipieView(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String
    ): Call<RecipieView>
}

