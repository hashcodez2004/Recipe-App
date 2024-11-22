package com.hashdroid.recipe_app.network

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import okhttp3.internal.Internal.instance

@Database(entities = [FavouritesEntity::class], version = 1)
abstract class FavouritesDB : RoomDatabase() {

    abstract fun favouritesDAO() : FavouritesDAO

    companion object{
        @Volatile
        private var INSTANCE : FavouritesDB? = null

        fun getDatabase(context: Context): FavouritesDB {
            return INSTANCE ?: synchronized(this) {
                // If INSTANCE is null, create the database instance
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavouritesDB::class.java,
                    "FavouritesDB"
                ).build()
                INSTANCE = instance
                instance // Return the newly created instance
            }
        }
    }
}