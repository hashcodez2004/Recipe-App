package com.hashdroid.recipe_app.network

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class FavouritesEntity(
    @PrimaryKey
    val id: Int,
    val img_title : String,
    val img_url : String,
    val cooking_time : Int
)
