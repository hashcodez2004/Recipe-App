package com.hashdroid.recipe_app.network

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavouritesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(entity : FavouritesEntity)

    @Query("SELECT * FROM favourites")
    fun getAllFavorites(): LiveData<List<FavouritesEntity>>

    @Delete
    suspend fun removeFromFavorites(entity: FavouritesEntity)
}