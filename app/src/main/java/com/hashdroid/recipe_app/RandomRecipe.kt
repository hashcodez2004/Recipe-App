package com.hashdroid.recipe_app

import com.hashdroid.recipe_app.network.ApiKey
import com.hashdroid.recipe_app.network.Recipe
import com.hashdroid.recipe_app.network.RecipeResponse
import com.hashdroid.recipe_app.network.RecipeResponse2
import com.hashdroid.recipe_app.network.RecipieView
import com.hashdroid.recipe_app.network.SimilarRecipes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface RecipeApiService {
    @GET("recipes/random")
    fun getRandomRecipes(
        @Query("number") number: Int,
        @Query("apiKey") apiKey: String = ApiKey.apiKey
    ): Call<RecipeResponse>

    @GET("recipes/complexSearch")
    fun getAllRecipes(
        @Query("number") number: Int,
        @Query("apiKey") apiKey: String = ApiKey.apiKey
    ): Call<RecipeResponse2>

    @GET("recipes/{id}/information")
    fun getRecipieView(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String = ApiKey.apiKey
    ): Call<RecipieView>

    @GET("recipes/autocomplete")
    fun getAutocompleteRecipes(
        @Query("query") query: String?,
        @Query("apiKey") apiKey: String = ApiKey.apiKey
    ): Call<List<Recipe>>

    @GET("recipes/{id}/similar")
    fun getSimilarRecipes(
        @Path("id") id: Int,
        @Query("number") number: Int = 10,
        @Query("apiKey") apiKey: String = ApiKey.apiKey
    ): Call<List<SimilarRecipes>>
}

